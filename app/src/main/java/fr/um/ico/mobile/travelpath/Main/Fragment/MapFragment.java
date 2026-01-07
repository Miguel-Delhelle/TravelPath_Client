package fr.um.ico.mobile.travelpath.Main.Fragment;

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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.R;

public class MapFragment extends Fragment {

    private MapView map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        map = view.findViewById(R.id.map);
        map.setMultiTouchControls(true);
        MapController mapController = (MapController) map.getController();
        mapController.setZoom(12.0);
        mapController.setCenter(new GeoPoint(43.6112, 3.8767)); // Défaut
    }

    // --- API PUBLIQUE POUR L'ACTIVITY ---

    public void centerOnUser(double lat, double lon) {
        if (map == null) return;
        GeoPoint userPos = new GeoPoint(lat, lon);
        map.getController().animateTo(userPos);
        map.getController().setZoom(15.0);
    }

    public void displayRoute(List<PoiDto> pois, double startLat, double startLon) {
        if (map == null) return;

        map.getOverlays().clear();
        List<GeoPoint> routePoints = new ArrayList<>();

        // 1. Départ
        GeoPoint start = new GeoPoint(startLat, startLon);
        routePoints.add(start);
        addMarker(start, "Départ", "Vous êtes ici", true);

        // 2. Étapes
        int step = 1;
        for (PoiDto poi : pois) {
            GeoPoint point = new GeoPoint(poi.getLat(), poi.getLon());
            routePoints.add(point);
            String snippet = poi.getCategory() + " (" + poi.getEstimatedCost() + "€)";
            addMarker(point, step + ". " + poi.getName(), snippet, false);
            step++;
        }

        // 3. Ligne Rouge
        Polyline line = new Polyline();
        line.setPoints(routePoints);
        line.setColor(Color.parseColor("#7F1734"));
        line.setWidth(15.0f);
        line.setGeodesic(true);

        map.getOverlays().add(0, line);
        map.invalidate();
    }

    private void addMarker(GeoPoint pos, String title, String snippet, boolean isStart) {
        Marker marker = new Marker(map);
        marker.setPosition(pos);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        // Ici tu peux changer l'icône selon isStart
        map.getOverlays().add(marker);
    }

    // Lifecycle OSM...
    @Override public void onResume() { super.onResume(); if(map!=null) map.onResume(); }
    @Override public void onPause() { super.onPause(); if(map!=null) map.onPause(); }
}