package fr.um.ico.mobile.travelpath.di;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import fr.um.ico.mobile.travelpath.Data.Api.TravelPathApi;
import fr.um.ico.mobile.travelpath.Data.Repository.TripRepositoryImpl;
import fr.um.ico.mobile.travelpath.Data.Local.UserPrefsManager;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Gestionnaire manuel d'Injection de Dépendance.
 * Il contient les Singletons de l'application.
 */
public class AppContainer {

    private final Context context;

    // Instances Uniques (Singletons)
    private Retrofit retrofit;
    private TravelPathApi travelPathApi;
    private TripRepositoryImpl tripRepository;
    private UserPrefsManager userPrefsManager;

    public AppContainer(Context context) {
        this.context = context;
    }

    // --- NETWORK ---
    public Retrofit getRetrofit() {
        if (retrofit == null) {
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

            retrofit = new Retrofit.Builder()
                    .baseUrl("https://travelpath.delweb.fr/") // Prod URL
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public TravelPathApi getTravelPathApi() {
        if (travelPathApi == null) {
            travelPathApi = getRetrofit().create(TravelPathApi.class);
        }
        return travelPathApi;
    }

    // --- REPOSITORIES ---
    public TripRepositoryImpl getTripRepository() {
        if (tripRepository == null) {
            tripRepository = new TripRepositoryImpl(getTravelPathApi());
        }
        return tripRepository;
    }

    // --- UTILS ---
    public UserPrefsManager getUserPrefsManager() {
        if (userPrefsManager == null) {
            userPrefsManager = new UserPrefsManager(context);
        }
        return userPrefsManager;
    }
}