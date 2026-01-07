package fr.um.ico.mobile.travelpath.ui.map;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import java.util.ArrayList;
import java.util.List;

// --- CHANGEMENT CRITIQUE : IMPORT DU DOMAINE ---
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.databinding.FragmentMapBinding;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;

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

        binding.map.setMultiTouchControls(true);
        MapController mapController = (MapController) binding.map.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new GeoPoint(43.6112, 3.8767));
    }

    public void centerOnUser(double lat, double lon) {
        if (binding == null) return;
        GeoPoint userPos = new GeoPoint(lat, lon);
        binding.map.getController().animateTo(userPos);
        binding.map.getController().setZoom(15.0);
    }

    // --- ICI : On utilise PointOfInterest (Domain) ---
    public void displayRoute(List<PointOfInterest> pois, double startLat, double startLon) {
        if (binding == null) return;

        binding.map.getOverlays().clear();
        List<GeoPoint> routePoints = new ArrayList<>();

        GeoPoint start = new GeoPoint(startLat, startLon);
        routePoints.add(start);
        addMarker(start, "Départ", "Vous êtes ici");

        int step = 1;
        for (PointOfInterest poi : pois) {
            GeoPoint point = new GeoPoint(poi.getLat(), poi.getLon());
            routePoints.add(point);
            String snippet = poi.getCategory() + " (" + poi.getEstimatedCost() + "€)";
            addMarker(point, step + ". " + poi.getName(), snippet);
            step++;
        }

        Polyline line = new Polyline();
        line.setPoints(routePoints);
        line.setColor(Color.parseColor("#7F1734"));
        line.setWidth(15.0f);
        line.setGeodesic(true);

        binding.map.getOverlays().add(0, line);
        binding.map.invalidate();
    }

    private void addMarker(GeoPoint pos, String title, String snippet) {
        Marker marker = new Marker(binding.map);
        marker.setPosition(pos);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        binding.map.getOverlays().add(marker);
    }

    @Override
    public void onResume() { super.onResume(); if(binding!=null) binding.map.onResume(); }
    @Override
    public void onPause() { super.onPause(); if(binding!=null) binding.map.onPause(); }
    @Override
    public void onDestroyView() { super.onDestroyView(); binding = null; }
}