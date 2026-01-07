package fr.um.ico.mobile.travelpath.Main.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

public class BottomBarFragment extends Fragment {

    private TravelPathApi apiService;
    private LocationHelper locationHelper;
    private MapUpdateListener mapUpdateListener;

    // Interface pour communiquer uniquement le résultat à l'Activity
    public interface MapUpdateListener {
        void onRouteFound(List<PoiDto> pois, double userLat, double userLon);
        void onUserOptionClicked(); // Pour le bouton de droite
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MapUpdateListener) {
            mapUpdateListener = (MapUpdateListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement MapUpdateListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Init Location Helper
        locationHelper = new LocationHelper(requireActivity());

        // 2. Init Retrofit
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("X-API-KEY", "zoov5Ohphohthae0geiheeph9uxae5Pheif7reequ6naad3yieMaiquei4yaeg7Y")
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        apiService = new Retrofit.Builder()
                .baseUrl("https://travelpath.delweb.fr/") // PROD
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TravelPathApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);

        // --- Bouton SEARCH (Central) ---
        view.findViewById(R.id.btn_search_path).setOnClickListener(v -> {
            performFullSearch();
        });

        // --- Bouton OPTIONS (Droite) ---
        view.findViewById(R.id.btn_user_options).setOnClickListener(v -> {
            if (mapUpdateListener != null) mapUpdateListener.onUserOptionClicked();
        });

        // --- Bouton GAUCHE (Placeholder) ---
        view.findViewById(R.id.btn_left_placeholder).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Mystère...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // --- 1. Recherche Standard (Bouton GO) ---
    public void performFullSearch() {
        Toast.makeText(getContext(), "Acquisition GPS...", Toast.LENGTH_SHORT).show();
        locationHelper.getLastLocation((lat, lon) -> {
            // Recherche standard = blacklist vide (null)
            executeSearchRequest(lat, lon, null);
        });
    }

    // --- 2. Recherche avec Exclusion (Swipe TopBar) ---
    // Cette méthode est appelée par MainActivity quand on swipe une carte
    public void performSearchWithBlacklist(List<Long> currentBlacklist) {
        Toast.makeText(getContext(), "Recalcul d'itinéraire...", Toast.LENGTH_SHORT).show();
        locationHelper.getLastLocation((lat, lon) -> {
            executeSearchRequest(lat, lon, currentBlacklist);
        });
    }

    // --- LOGIQUE COMMUNE D'ENVOI ---
    private void executeSearchRequest(double lat, double lon, List<Long> blacklist) {
        Toast.makeText(getContext(), "Envoi au serveur...", Toast.LENGTH_SHORT).show();

        // 1. Profil Utilisateur (Hardcodé pour l'instant)
        Map<String, Double> interests = new HashMap<>();
        interests.put("CULTURE", 0.8);
        interests.put("FOOD", 0.6);
        interests.put("FUN", 0.5);
        interests.put("NIGHT_LIFE", 0.0);
        UserPreferencesDto userProfile = new UserPreferencesDto(interests);

        // 2. Contexte
        String currentMood = "ACTIVE";
        String currentWeather = "SUNNY";
        String localTime = LocalTime.now().toString().substring(0, 8);

        // 3. Création du DTO
        MoodRequestDto request = new MoodRequestDto(lat, lon, currentMood, currentWeather, localTime, userProfile);

        // 4. Injection de la Blacklist (SI ELLE EXISTE)
        if (blacklist != null && !blacklist.isEmpty()) {
            request.setBlacklistedIds(blacklist);
        }

        // 5. Appel API
        apiService.searchByMood(request).enqueue(new Callback<List<PoiDto>>() {
            @Override
            public void onResponse(Call<List<PoiDto>> call, Response<List<PoiDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (mapUpdateListener != null) {
                        mapUpdateListener.onRouteFound(response.body(), lat, lon);
                    }
                } else {
                    Toast.makeText(getContext(), "Erreur Serveur: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PoiDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur Réseau: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}