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
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.um.ico.mobile.travelpath.Model.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.Model.TripDto;
import fr.um.ico.mobile.travelpath.Model.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.Network.TravelPathApi;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.Utils.LocationHelper;
import fr.um.ico.mobile.travelpath.Utils.UserPrefsManager; // Import important
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

    // --- ETAT LOCAL (Mémoire du trajet en cours) ---
    private MoodRequestDto lastRequest; // Le contexte initial (Mood, Météo, etc.)
    private List<PoiDto> currentPath;   // La liste des points actuelle

    public interface MapUpdateListener {
        void onRouteFound(List<PoiDto> pois, double userLat, double userLon);
        void onUserOptionClicked();
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
        locationHelper = new LocationHelper(requireActivity());

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(chain -> {
                    okhttp3.Request original = chain.request();
                    okhttp3.Request request = original.newBuilder()
                            .header("X-API-KEY", "zoov5Ohphohthae0geiheeph9uxae5Pheif7reequ6naad3yieMaiquei4yaeg7Y") // Ta clé
                            .method(original.method(), original.body())
                            .build();
                    return chain.proceed(request);
                })
                .build();

        apiService = new Retrofit.Builder()
                .baseUrl("https://travelpath.delweb.fr/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TravelPathApi.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);

        view.findViewById(R.id.btn_search_path).setOnClickListener(v -> performFullSearch());

        view.findViewById(R.id.btn_user_options).setOnClickListener(v -> {
            if (mapUpdateListener != null) mapUpdateListener.onUserOptionClicked();
        });

        view.findViewById(R.id.btn_left_placeholder).setOnClickListener(v -> {
            Toast.makeText(getContext(), "Soon...", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    // --- 1. RECHERCHE INITIALE (Bouton GO) ---
    public void performFullSearch() {
        Toast.makeText(getContext(), "Acquisition GPS...", Toast.LENGTH_SHORT).show();

        locationHelper.getLastLocation((lat, lon) -> {
            // On appelle la méthode générique avec une blacklist vide (null)
            executeSearchRequest(lat, lon, null);
        });
    }

    // --- 2. MÉTHODE GÉNÉRIQUE D'ENVOI (Celle qui manquait !) ---
    private void executeSearchRequest(double lat, double lon, List<Long> blacklist) {
        Toast.makeText(getContext(), "Calcul itinéraire...", Toast.LENGTH_SHORT).show();

        // A. CHARGEMENT DYNAMIQUE DES PRÉFÉRENCES (Plus de hardcode !)
        UserPrefsManager prefsManager = new UserPrefsManager(requireContext());
        UserPreferencesDto userProfile = prefsManager.loadPreferences();

        // B. Contexte (Mood + Météo + Heure)
        String currentMood = "ACTIVE";
        String currentWeather = "SUNNY";
        String localTime = LocalTime.now().toString().substring(0, 8);

        // C. Création et SAUVEGARDE de la requête
        lastRequest = new MoodRequestDto(lat, lon, currentMood, currentWeather, localTime, userProfile);

        // Si on a une blacklist manuelle (cas rare ici mais prévu), on l'ajoute
        if (blacklist != null) {
            lastRequest.setBlacklistedIds(blacklist);
        }

        // D. Appel API
        apiService.searchByMood(lastRequest).enqueue(new Callback<List<PoiDto>>() {
            @Override
            public void onResponse(Call<List<PoiDto>> call, Response<List<PoiDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPath = response.body(); // Sauvegarde
                    if (mapUpdateListener != null) {
                        mapUpdateListener.onRouteFound(currentPath, lat, lon);
                    }
                } else {
                    Toast.makeText(getContext(), "Erreur Serveur: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<PoiDto>> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur Réseau", Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- 3. RECALCUL INTELLIGENT (Appelé par MainActivity via Swipe) ---
    public void recalculateWithBanned(PoiDto bannedPoi) {
        // Sécurité
        if (lastRequest == null || currentPath == null) {
            Toast.makeText(getContext(), "Aucun trajet actif à modifier", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Suppression de l'étape...", Toast.LENGTH_SHORT).show();

        // On construit l'objet TripDto
        TripDto tripDto = new TripDto(currentPath, bannedPoi, lastRequest);

        apiService.recalculateTrip(tripDto).enqueue(new Callback<TripDto>() {
            @Override
            public void onResponse(Call<TripDto> call, Response<TripDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripDto newTripResult = response.body();

                    // Mise à jour locale
                    currentPath = newTripResult.getPath();
                    lastRequest = newTripResult.getContext(); // Met à jour la blacklist incluse

                    if (mapUpdateListener != null) {
                        mapUpdateListener.onRouteFound(currentPath, lastRequest.getLat(), lastRequest.getLon());
                    }
                } else {
                    Toast.makeText(getContext(), "Erreur Recalcul: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripDto> call, Throwable t) {
                Toast.makeText(getContext(), "Erreur Réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }
}