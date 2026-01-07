package fr.um.ico.mobile.travelpath.Data.Api;

import java.util.List;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripRequestDto;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TravelPathApi {

    // L'endpoint "Magique" (Mood + Profil + Météo)
    @POST("api/trip/mood")
    Call<List<PoiDto>> searchByMood(@Body MoodRequestDto request);

    // L'endpoint "Manuel" (Profil uniquement)
    @POST("api/trip/search")
    Call<List<PoiDto>> searchManual(@Body TripRequestDto request);

    @POST("api/trip/recalculate")
    Call<TripDto> recalculateTrip(@Body TripDto trip);
}