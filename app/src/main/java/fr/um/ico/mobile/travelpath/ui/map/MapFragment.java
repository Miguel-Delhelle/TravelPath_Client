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
    private MyLocationNewOverlay locationOverlay;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // --- NOUVEAU : Gestion des marqueurs ---
    private final List<Marker> stepMarkers = new ArrayList<>();
    private OnMarkerClickListener markerListener;

    public interface OnMarkerClickListener {
        void onMarkerClicked(int position);
    }

    public void setOnMarkerClickListener(OnMarkerClickListener listener) {
        this.markerListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    private void setupMapUI() {
        binding.map.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        binding.map.setTilesScaledToDpi(true);
        binding.map.setHorizontalMapRepetitionEnabled(false);
        binding.map.setVerticalMapRepetitionEnabled(false);
        binding.map.setHasTransientState(true);
        binding.map.setMultiTouchControls(true);
        binding.map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        RotationGestureOverlay rotationGestureOverlay = new RotationGestureOverlay(binding.map);
        rotationGestureOverlay.setEnabled(true);
        binding.map.getOverlays().add(rotationGestureOverlay);

        MapController mapController = (MapController) binding.map.getController();
        mapController.setZoom(15.0);
    }

    private void setupLocationOverlay() {
        GpsMyLocationProvider provider = new GpsMyLocationProvider(requireContext());
        provider.addLocationSource(android.location.LocationManager.GPS_PROVIDER);
        provider.addLocationSource(android.location.LocationManager.NETWORK_PROVIDER);

        locationOverlay = new MyLocationNewOverlay(provider, binding.map);
        Bitmap blueDot = getBitmapFromVectorDrawable(R.drawable.ic_my_location_dot);
        if (blueDot != null) {
            locationOverlay.setPersonIcon(blueDot);
            locationOverlay.setDirectionIcon(blueDot);
        }
        locationOverlay.enableMyLocation();
        locationOverlay.enableFollowLocation();
        locationOverlay.setDrawAccuracyEnabled(false);

        binding.map.getOverlays().add(locationOverlay);
        binding.map.getController().setZoom(18.0);
    }

    public void displayRoute(List<PointOfInterest> pois, double startLat, double startLon) {
        if (binding == null) return;
        locationOverlay.disableFollowLocation();

        // 1. Nettoyage complet
        binding.map.getOverlays().removeIf(overlay -> overlay instanceof Marker || overlay instanceof Polyline);
        stepMarkers.clear(); // Important : on vide la liste des références

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(new GeoPoint(startLat, startLon));

        // 2. Création des marqueurs avec index
        int stepIndex = 0;
        for (PointOfInterest poi : pois) {
            GeoPoint point = new GeoPoint(poi.getLat(), poi.getLon());
            waypoints.add(point);
            // On passe l'index pour le callback
            addMarker(point, (stepIndex + 1) + ". " + poi.getName(), poi.getCategory(), stepIndex);
            stepIndex++;
        }

        // 3. Calcul Route (Reste identique)
        executor.execute(() -> {
            try {
                RoadManager roadManager = new OSRMRoadManager(requireContext(), "TravelPathAgent");
                ((OSRMRoadManager) roadManager).setMean(OSRMRoadManager.MEAN_BY_FOOT);
                Road road = roadManager.getRoad(waypoints);

                mainHandler.post(() -> {
                    if (binding != null && road.mStatus == Road.STATUS_OK) {
                        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
                        roadOverlay.setWidth(15.0f);
                        roadOverlay.getOutlinePaint().setColor(ContextCompat.getColor(requireContext(), R.color.travelpath_purple));

                        binding.map.getOverlays().add(0, roadOverlay);
                        binding.map.invalidate();
                        binding.map.zoomToBoundingBox(road.mBoundingBox, true);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // Modifié pour gérer l'index et le clic
    private void addMarker(GeoPoint pos, String title, String snippet, int index) {
        Marker marker = new Marker(binding.map);
        marker.setPosition(pos);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        Drawable icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location_pin);
        if (icon != null) marker.setIcon(icon);

        // --- CLIC SUR MARKER ---
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            mapView.getController().animateTo(m.getPosition());
            // On notifie l'activité
            if (markerListener != null) {
                markerListener.onMarkerClicked(index);
            }
            return true;
        });

        binding.map.getOverlays().add(marker);
        stepMarkers.add(marker); // On sauvegarde la référence
    }

    // --- Appelée par l'Activité quand on clique sur la liste ---
    public void focusOnStep(int index) {
        if (index >= 0 && index < stepMarkers.size()) {
            Marker target = stepMarkers.get(index);
            // Animation fluide
            binding.map.getController().animateTo(target.getPosition());
            target.showInfoWindow(); // Ouvre la bulle
        }
    }

    // ... (getBitmapFromVectorDrawable, centerOnUser, lifecycle... inchangés) ...
    private Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), drawableId);
        if (drawable == null) return null;
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
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