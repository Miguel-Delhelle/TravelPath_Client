package fr.um.ico.mobile.travelpath.ui.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.ui.trip.TripControlFragment;
import fr.um.ico.mobile.travelpath.ui.map.MapFragment;
import fr.um.ico.mobile.travelpath.ui.trip.TopBarFragment;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.ui.settings.UserSettingsActivity;
import fr.um.ico.mobile.travelpath.domain.Utils.LocationHelper;
import fr.um.ico.mobile.travelpath.databinding.ActivityMainBinding;
import com.security.detector.SecurityDetector;

public class MainActivity extends AppCompatActivity implements
        TripControlFragment.MapUpdateListener,
        TopBarFragment.OnStepActionListener,
        MapFragment.OnMarkerClickListener { // Implémentation de la nouvelle interface Map

    private ActivityMainBinding binding;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SecurityDetector.checkSecurityAndRemediate(this);
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationHelper = new LocationHelper(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            centerMapOnUser();
        }
    }

    private void centerMapOnUser() {
        locationHelper.getLastLocation((lat, lon) -> {
            MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            if (mapFrag != null) {
                mapFrag.centerOnUser(lat, lon);
            }
        });
    }

    @Override
    public void onRouteFound(List<PointOfInterest> pois, double userLat, double userLon) {
        MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFrag != null) {
            // On s'abonne aux clics marqueurs
            mapFrag.setOnMarkerClickListener(this);
            mapFrag.displayRoute(pois, userLat, userLon);
        }

        TopBarFragment topFrag = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_top_bar);
        if (topFrag != null) topFrag.updateSteps(pois);
    }

    // --- LISTE -> MAP ---
    @Override
    public void onStepClicked(int position) {
        MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFrag != null) {
            mapFrag.focusOnStep(position);
        }
    }

    // --- MAP -> LISTE ---
    @Override
    public void onMarkerClicked(int position) {
        TopBarFragment topFrag = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_top_bar);
        if (topFrag != null) {
            topFrag.scrollToStep(position);
        }
    }

    @Override
    public void onUserOptionClicked() {
        startActivity(new Intent(this, UserSettingsActivity.class));
    }

    @Override
    public void onStepSwiped(long osmIdToBan) {
        TripControlFragment bottomFrag = (TripControlFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_bar);
        if (bottomFrag != null) {
            bottomFrag.recalculateWithBanned(osmIdToBan);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                centerMapOnUser();
            } else {
                Toast.makeText(this, "GPS requis pour fonctionner", Toast.LENGTH_LONG).show();
            }
        }
    }
}