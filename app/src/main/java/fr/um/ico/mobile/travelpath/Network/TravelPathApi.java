package fr.um.ico.mobile.travelpath.Network;

import java.util.List;
import fr.um.ico.mobile.travelpath.Model.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.Model.TripRequestDto;
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
}