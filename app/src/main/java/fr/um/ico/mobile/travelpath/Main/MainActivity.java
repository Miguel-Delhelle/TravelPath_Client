package fr.um.ico.mobile.travelpath.Main;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import fr.um.ico.mobile.travelpath.Main.Fragment.BottomBarFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.MapFragment;
import fr.um.ico.mobile.travelpath.Model.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.Model.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.Network.TravelPathApi;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.Utils.LocationHelper;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements BottomBarFragment.OnApiRequestListener {

    private TravelPathApi apiService;
    private LocationHelper locationHelper;
    private static final String BASE_URL = "https://travelpath.delweb.fr/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Init Helpers
        locationHelper = new LocationHelper(this);

        // 2. Init Retrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).build();

        apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TravelPathApi.class);
    }

    // Callback du BottomBarFragment
    @Override
    public void onApiRequest() {
        // L'Activity demande la position au Helper
        locationHelper.getLastLocation((lat, lon) -> {
            // Une fois la position reçue, on orchestre la suite

            // A. Mettre à jour la carte
            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
            if (mapFragment != null) {
                mapFragment.centerOnUser(lat, lon);
            }

            // B. Lancer l'appel réseau
            performSearch(lat, lon);
        });
    }

    private void performSearch(double lat, double lon) {
        Toast.makeText(this, "Recherche intelligente...", Toast.LENGTH_SHORT).show();

        // Création du contexte (Profil + Mood) - À terme, viendra d'un ViewModel ou Singleton UserSession
        Map<String, Double> interests = new HashMap<>();
        interests.put("NATURE", 0.9);
        interests.put("NIGHT_LIFE", 0.8);
        UserPreferencesDto userProfile = new UserPreferencesDto(interests);

        MoodRequestDto request = new MoodRequestDto(lat, lon, "PARTY", "SUNNY", "21:00:00", userProfile);

        apiService.searchByMood(request).enqueue(new Callback<List<PoiDto>>() {
            @Override
            public void onResponse(Call<List<PoiDto>> call, Response<List<PoiDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // L'Activity passe les résultats au MapFragment pour affichage
                    MapFragment mapFrag = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                    if (mapFrag != null) {
                        mapFrag.displayRoute(response.body(), lat, lon);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Erreur Serveur", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PoiDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur Connexion", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Gestion du callback de permission pour le LocationHelper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si l'utilisateur accepte, on relance l'action
                onApiRequest();
            }
        }
    }
}