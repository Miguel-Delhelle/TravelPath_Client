package fr.um.ico.mobile.travelpath.Main;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline; // C'est ça qui fait le chemin !

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.um.ico.mobile.travelpath.Main.Fragment.BottomBarFragment;
import fr.um.ico.mobile.travelpath.Main.Fragment.MapFragment;
import fr.um.ico.mobile.travelpath.Model.ContextualSearchDto;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.Network.TravelPathApi;
import fr.um.ico.mobile.travelpath.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements BottomBarFragment.OnApiRequestListener {

    private TravelPathApi apiService;
    // IP émulateur: 10.0.2.2 | IP Phone: 192.168.X.X
    private static final String BASE_URL = "http://10.0.2.2:8080/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Temps pour établir la connexion
                .readTimeout(60, TimeUnit.SECONDS)    // Temps d'attente de la réponse (le plus important ici)
                .writeTimeout(60, TimeUnit.SECONDS)   // Temps d'envoi des données
                .build();

        // 2. L'injecter dans Retrofit
        try {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient) // <--- AJOUT IMPORTANT
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            apiService = retrofit.create(TravelPathApi.class);
        } catch (Exception e) {
            Log.e("RETROFIT", "Erreur init: " + e.getMessage());
        }
    }

    @Override
    public void onApiRequest() {
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFragment == null || mapFragment.getMap() == null) return;

        // 1. DTO HARDCODÉ (Active / Sunny / 15:00 / Montpellier)
        ContextualSearchDto request = new ContextualSearchDto(
                43.6112,
                3.8767,
                "ACTIVE",
                "SUNNY",
                "15:00:00"
        );

        Toast.makeText(this, "Génération de l'itinéraire...", Toast.LENGTH_SHORT).show();

        // 2. Appel API
        apiService.planTripContext(request).enqueue(new Callback<List<PoiDto>>() {
            @Override
            public void onResponse(Call<List<PoiDto>> call, Response<List<PoiDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // On affiche le chemin
                    displayRouteOnMap(response.body(), mapFragment);
                    Toast.makeText(MainActivity.this, "Itinéraire trouvé !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Erreur Serveur: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PoiDto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur Connexion: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("API_ERROR", t.getMessage());
            }
        });
    }

    // Méthode pour dessiner les points ET la ligne qui les relie
    private void displayRouteOnMap(List<PoiDto> pois, MapFragment mapFragment) {
        if (mapFragment.getMap() == null) return;

        // Nettoyer la carte précédente
        mapFragment.getMap().getOverlays().clear();

        // Liste pour stocker les points GPS pour la ligne
        List<GeoPoint> routePoints = new ArrayList<>();

        // Ajouter le point de départ (ta position hardcodée)
        GeoPoint startPoint = new GeoPoint(43.6112, 3.8767);
        routePoints.add(startPoint);
        addMarker(mapFragment, startPoint, "Départ", "Vous êtes ici", true);

        int step = 1;
        for (PoiDto poi : pois) {
            if (poi.getLat() != null && poi.getLon() != null) {
                GeoPoint point = new GeoPoint(poi.getLat(), poi.getLon());
                routePoints.add(point);

                // On ajoute un marqueur pour chaque étape
                addMarker(mapFragment, point, step + ". " + poi.getName(), poi.getCategory(), false);
                step++;
            }
        }

        // --- DESSIN DU CHEMIN (POLYLINE) ---
        Polyline line = new Polyline();
        line.setPoints(routePoints);
        line.setColor(Color.parseColor("#7F1734")); // Ton rouge bordeaux ;)
        line.setWidth(15.0f); // Épaisseur du trait

        // Optionnel : rendre la ligne géodésique (courbure de la terre)
        line.setGeodesic(true);

        // Ajouter la ligne à la carte (en premier pour qu'elle soit SOUS les marqueurs si possible, sinon index 0)
        mapFragment.getMap().getOverlays().add(0, line);

        mapFragment.getMap().invalidate(); // Rafraîchir

        // Centrer la vue sur le départ
        mapFragment.getMap().getController().animateTo(startPoint);
    }

    private void addMarker(MapFragment mapFragment, GeoPoint position, String title, String snippet, boolean isStart) {
        Marker marker = new Marker(mapFragment.getMap());
        marker.setPosition(position);
        marker.setTitle(title);
        marker.setSnippet(snippet);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

        // Tu pourras personnaliser l'icône ici plus tard si tu veux
        // if(isStart) marker.setIcon(...);

        mapFragment.getMap().getOverlays().add(marker);
    }
}