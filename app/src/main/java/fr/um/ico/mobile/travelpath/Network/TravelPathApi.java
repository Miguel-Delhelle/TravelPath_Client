package fr.um.ico.mobile.travelpath.Network;

import java.util.List;

import fr.um.ico.mobile.travelpath.Model.ContextualSearchDto;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.Model.SearchRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TravelPathApi {

    /**
     * Envoie la position et les préférences, reçoit une liste de points d'intérêt.
     * Correspond au endpoint SpringBoot : @PostMapping("/api/trip/search")
     */
    @POST("api/trip/search")
    Call<List<PoiDto>> searchPois(@Body SearchRequestDto request);

    /**
     * Pour plus tard : générer un itinéraire complet.
     * Correspond au endpoint SpringBoot : @PostMapping("/api/trip/plan")
     */
    @POST("api/trip/plan")
    Call<List<PoiDto>> planTrip(@Body SearchRequestDto request);

    @POST("api/trip/plan-context")
    Call<List<PoiDto>> planTripContext(@Body ContextualSearchDto request);
}