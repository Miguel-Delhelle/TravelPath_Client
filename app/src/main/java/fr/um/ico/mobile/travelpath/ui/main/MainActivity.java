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
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.ui.settings.UserSettingsActivity;
import fr.um.ico.mobile.travelpath.domain.Utils.LocationHelper;
import fr.um.ico.mobile.travelpath.databinding.ActivityMainBinding; // Généré
public class MainActivity extends AppCompatActivity implements TripControlFragment.MapUpdateListener, TopBarFragment.OnStepActionListener {

    private ActivityMainBinding binding;
    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                // C'est ici qu'on force le centrage au lancement de l'app
                mapFrag.centerOnUser(lat, lon);
            }
        });
    }

    @Override
    public void onRouteFound(List<PointOfInterest> pois, double userLat, double userLon) { // TYPE CORRIGÉ
        MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFrag != null) {
            mapFrag.displayRoute(pois, userLat, userLon);
            //mapFrag.centerOnUser(userLat, userLon);
        }

        TopBarFragment topFrag = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_top_bar);
        if (topFrag != null) topFrag.updateSteps(pois);
    }

    @Override
    public void onUserOptionClicked() {
        startActivity(new Intent(this, UserSettingsActivity.class));
    }

    @Override
    public void onStepSwiped(long osmIdToBan) {
        TripControlFragment bottomFrag = (TripControlFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_bar);
        if (bottomFrag != null) {
            // Plus besoin de créer un PoiDto ici ! Juste l'ID.
            bottomFrag.recalculateWithBanned(osmIdToBan);
        } else {
            Toast.makeText(this, "Erreur interne", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GPS accordée !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS requis pour fonctionner", Toast.LENGTH_LONG).show();
            }
        }
    }
}