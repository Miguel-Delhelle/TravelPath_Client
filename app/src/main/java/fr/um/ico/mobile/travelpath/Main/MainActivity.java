package fr.um.ico.mobile.travelpath.Main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import fr.um.ico.mobile.travelpath.Main.Fragment.BottomBarFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.MapFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.TopBarFragment;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.UsersSettings.UserActivity;
import fr.um.ico.mobile.travelpath.Utils.LocationHelper;

public class MainActivity extends AppCompatActivity implements BottomBarFragment.MapUpdateListener, TopBarFragment.OnStepActionListener {

    private LocationHelper locationHelper; // On le garde ici pour la gestion des permissions callback
    private List<Long> currentBlacklist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Juste pour gérer les retours de permissions Android
        locationHelper = new LocationHelper(this);
    }

    // --- Implémentation de l'interface du Fragment ---

    @Override
    public void onRouteFound(List<PoiDto> pois, double userLat, double userLon) {
        // 1. Update Map (Existant)
        MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFrag != null) mapFrag.displayRoute(pois, userLat, userLon);

        // 2. Update TopBar (Nouveau)
        TopBarFragment topFrag = (TopBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_top_bar);
        if (topFrag != null) topFrag.updateSteps(pois);
    }

    @Override
    public void onUserOptionClicked() {
        startActivity(new Intent(this, UserActivity.class));
    }


    // --- Gestion Permissions ---
    // Obligé de rester dans l'Activity car c'est elle qui reçoit le résultat système
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GPS accordée, réessayez !", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS requis pour fonctionner", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStepSwiped(long osmIdToBan) {
        // 1. On ajoute à la liste noire
        currentBlacklist.add(osmIdToBan);
        Toast.makeText(this, "Lieu supprimé ! Recalcul...", Toast.LENGTH_SHORT).show();

        // 2. On demande au BottomFragment de relancer la recherche
        // (Idéalement, déplace la méthode performSearch dans MainActivity ou rend la publique dans le fragment)
        // Pour le MVP rapide, castons le fragment :
        BottomBarFragment bottomFrag = (BottomBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_bottom_bar);
        if (bottomFrag != null) {
            bottomFrag.performSearchWithBlacklist(currentBlacklist);
        }

    }
}