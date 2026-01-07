package fr.um.ico.mobile.travelpath.Main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import fr.um.ico.mobile.travelpath.Main.Fragment.BottomBarFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.MapFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.TopBarFragment;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.UsersSettings.UserActivity;
import fr.um.ico.mobile.travelpath.Utils.LocationHelper;

public class MainActivity extends AppCompatActivity implements BottomBarFragment.MapUpdateListener, TopBarFragment.OnStepActionListener {

    private LocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationHelper = new LocationHelper(this);
    }

    // --- BottomBarFragment dit : "J'ai un nouveau trajet !" ---
    @Override
    public void onRouteFound(List<PoiDto> pois, double userLat, double userLon) {
        // 1. Mise à jour de la Carte
        MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFrag != null) {
            mapFrag.displayRoute(pois, userLat, userLon);
            mapFrag.centerOnUser(userLat, userLon); // Optionnel : Recentrer
        }

        // 2. Mise à jour de la TopBar (Carrousel des étapes)
        TopBarFragment topFrag = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_top_bar);
        if (topFrag != null) {
            topFrag.updateSteps(pois);
        }
    }

    // --- Bouton Option cliqué ---
    @Override
    public void onUserOptionClicked() {
        startActivity(new Intent(this, UserActivity.class));
    }

    // --- TopBarFragment dit : "L'utilisateur a swipé ce lieu (ID)" ---
    @Override
    public void onStepSwiped(long osmIdToBan) {
        // On délègue la logique de recalcul au BottomFragment qui connaît le contexte (Mood, etc.)
        BottomBarFragment bottomFrag = (BottomBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_bar);

        if (bottomFrag != null) {
            // On reconstruit un objet POI minimal pour dire "Bannis celui-là"
            PoiDto itemToBan = new PoiDto();
            itemToBan.setId(osmIdToBan);

            bottomFrag.recalculateWithBanned(itemToBan);
        } else {
            Toast.makeText(this, "Erreur interne : Fragment introuvable", Toast.LENGTH_SHORT).show();
        }
    }

    // --- Gestion Permissions ---
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GPS accordée !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS requis", Toast.LENGTH_LONG).show();
            }
        }
    }
}