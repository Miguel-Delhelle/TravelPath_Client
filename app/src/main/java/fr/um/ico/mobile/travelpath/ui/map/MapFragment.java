package fr.um.ico.mobile.travelpath.ui.map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.databinding.FragmentMapBinding;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private MyLocationNewOverlay locationOverlay; // Le point bleu
    private final ExecutorService executor = Executors.newSingleThreadExecutor(); // Pour le calcul de route en fond
    private final Handler mainHandler = new Handler(Looper.getMainLooper()); // Pour revenir sur l'UI

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Configuration indispensable pour OSMDroid (User Agent)
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        binding = FragmentMapBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMapUI();
        setupLocationOverlay();
    }

    // --- 1. CONFIGURATION VISUELLE & PERFORMANCES ---
    private void setupMapUI() {
        // --- OPTIMISATIONS CRITIQUES (Anti-Lag) ---
        // 1. Force l'utilisation du GPU (Hardware Acceleration)
        binding.map.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // 2. Rend le texte des tuiles plus net sur les écrans haute densité
        binding.map.setTilesScaledToDpi(true);

        // 3. Empêche OSM de calculer le monde en boucle (Gros gain de FPS en rotation)
        binding.map.setHorizontalMapRepetitionEnabled(false);
        binding.map.setVerticalMapRepetitionEnabled(false);

        // 4. Optimisation du scroll
        binding.map.setHasTransientState(true);

        // --- UX / Gestures ---
        binding.map.setMultiTouchControls(true); // Pincement pour zoomer
        binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER); // Pas de boutons moches

        // Rotation avec deux doigts
        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(binding.map);
        rotationGestureOverlay.setEnabled(true);
        binding.map.getOverlays().add(rotationGestureOverlay);

        MapController mapController = (MapController) binding.map.getController();
        mapController.setZoom(15.0);
    }

    // --- 2. LOCALISATION LIVE (Point bleu) ---
    private void setupLocationOverlay() {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());
        // Augmente la fluidité de la mise à jour
        provider.addLocationSource(android.location.LocationManager.GPS_PROVIDER);
        provider.addLocationSource(android.location.LocationManager.NETWORK_PROVIDER);

        locationOverlay = new MyLocationNewOverlay(provider, binding.map);

        // --- 1. LE LOOK (Adieu le bonhomme jaune) ---
        Bitmap blueDot = getBitmapFromVectorDrawable(R.drawable.ic_my_location_dot);
        if (blueDot != null) {
            locationOverlay.setPersonIcon(blueDot);    // Quand tu es à l'arrêt
            locationOverlay.setDirectionIcon(blueDot); // Quand tu bouges (ou une flèche si tu préfères)
        }

        // --- 2. LE COMPORTEMENT (Mode Waze) ---
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation(); // La caméra suit le point
        locationOverlay.setDrawAccuracyEnabled(false); // On cache le gros cercle de précision (plus propre)

        binding.map.getOverlays().add(locationOverlay);

        // --- 3. ZOOM INITIAL (Niveau Rue) ---
        // On attend que la map soit prête pour zoomer et centrer
        binding.map.getController().setZoom(18.0);
    }

    // --- 3. ROUTING & AFFICHAGE ---
    public void displayRoute(List<PointOfInterest> pois, double startLat, double startLon) {
        if (binding == null) return;

        // On arrête de suivre l'utilisateur pour qu'il puisse explorer le trajet
        locationOverlay.disableFollowLocation();

        // Nettoyage intelligent : on enlève markers et routes, mais on garde le point bleu et la rotation
        binding.map.getOverlays().removeIf(overlay ->
                overlay instanceof Marker || overlay instanceof Polyline
        );

        // A. Préparation des waypoints
        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(new GeoPoint(startLat, startLon)); // Départ (User)

        int stepIndex = 1;
        for (PointOfInterest poi : pois) {
            GeoPoint point = new GeoPoint(poi.getLat(), poi.getLon());
            waypoints.add(point);
            addMarker(point, stepIndex + ". " + poi.getName(), poi.getCategory());
            stepIndex++;
        }

        // B. Calcul asynchrone via OSRM
        executor.execute(() -> {
            try {
                RoadManager roadManager = new OSRMRoadManager(requireContext(), "TravelPathAgent");
                ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT); // Mode Piéton

                Road road = roadManager.getRoad(waypoints);

                mainHandler.post(() -> {
                    if (binding != null && road.mStatus == Road.STATUS_OK) {
                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                        roadOverlay.setWidth(15.0f);

                        // Couleur du tracé : Violet TravelPath (#5E266E)
                        roadOverlay.getOutlinePaint().setColor(ContextCompat.getColor(requireContext(),R.color.travelpath_purple));

                        // Ajout en fond (index 0) pour que les markers soient au-dessus
                        binding.map.getOverlays().add(0, roadOverlay);
                        binding.map.invalidate();

                        // Zoom automatique sur le trajet
                        binding.map.zoomToBoundingBox(road.mBoundingBox, true);
                    } else {
                        Toast.makeText(getContext(), "Impossible de calculer l'itinéraire", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void addMarker(GeoPoint pos, String title, String snippet) {
        Marker marker = new Marker(binding.map);
        marker.setPosition(pos);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Utilisation directe du Drawable XML (déjà coloré en #5E266E dans le XML)
        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_pin);
        if (icon != null) {
            marker.setIcon(icon);
        }

        // Comportement au clic : focus + bulle d'info
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            mapView.getController().animateTo(m.getPosition());
            return true;
        });

        binding.map.getOverlays().add(marker);
    }

    private Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableId);
        if (drawable == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void centerOnUser(double lat, double lon) {
        if (binding == null) return;
        GeoPoint userPos = new GeoPoint(lat, lon);
        binding.map.getController().animateTo(userPos);
        locationOverlay.enableFollowLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (binding != null) binding.map.onResume();
        if (locationOverlay != null) locationOverlay.enableMyLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (binding != null) binding.map.onPause();
        if (locationOverlay != null) locationOverlay.disableMyLocation();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}