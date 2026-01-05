package fr.um.ico.mobile.travelpath.Main.Fragment;

import android.os.Bundle;
import android.preference.PreferenceManager; // Si déprécié, voir note plus bas
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

import fr.um.ico.mobile.travelpath.R;

public class MapFragment extends Fragment {

    private MapView map;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Initialisation de la config Osmdroid AVANT de gonfler la vue
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

        // Point de départ (Montpellier par exemple)
        GeoPoint startPoint = new GeoPoint(43.610769, 3.876716);
        mapController.setCenter(startPoint);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    }

    // Getter pour permettre à l'Activity d'ajouter des points
    public MapView getMap() {
        return map;
    }
}