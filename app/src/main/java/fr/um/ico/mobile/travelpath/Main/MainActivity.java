package fr.um.ico.mobile.travelpath.Main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import fr.um.ico.mobile.travelpath.R;

public class MainActivity extends AppCompatActivity {

    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Configuration.getInstance().setUserAgentValue(getPackageName());
        setContentView(R.layout.activity_main);

        map = findViewById(R.id.map);
        map.setMultiTouchControls(true);

        MapController mapController = (MapController) map.getController();
        mapController.setZoom(12.0);

        GeoPoint paris = new GeoPoint(43.62505, 3.862038);
        mapController.setCenter(paris);
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }
}