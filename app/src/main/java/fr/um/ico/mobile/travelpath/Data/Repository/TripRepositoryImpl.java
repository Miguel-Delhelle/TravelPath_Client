package fr.um.ico.mobile.travelpath.Data.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.um.ico.mobile.travelpath.Data.Api.Dto.FactoryDto;
import fr.um.ico.mobile.travelpath.Data.Api.TravelPathApi;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripDto;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.domain.repository.ITripRepository;
import retrofit2.Call;
import retrofit2.Response;

public class TripRepositoryImpl implements ITripRepository {

    private final TravelPathApi api;

    public TripRepositoryImpl(TravelPathApi api) {
        this.api = api;
    }

    @Override
    public void searchByMood(MoodRequestDto request, Callback<List<PointOfInterest>> callback) {
        api.searchByMood(request).enqueue(new retrofit2.Callback<List<PoiDto>>() {
            @Override
            public void onResponse(Call<List<PoiDto>> call, Response<List<PoiDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // UTILISATION DE FACTORY DTO VIA STREAM
                    List<PointOfInterest> domainList = response.body().stream()
                            .map(FactoryDto::from)
                            .collect(Collectors.toList());

                    callback.onSuccess(domainList);
                } else {
                    callback.onError("Erreur serveur : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<PoiDto>> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }

    @Override
    public void recalculateTrip(TripDto tripDto, Callback<List<PointOfInterest>> callback) {
        api.recalculateTrip(tripDto).enqueue(new retrofit2.Callback<TripDto>() {

            @Override
            public void onResponse(Call<TripDto> call, Response<TripDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<PoiDto> dtoList = response.body().getPath();
                    List<PointOfInterest> domainList = dtoList.stream()
                            .map(FactoryDto::from)
                            .collect(Collectors.toList());

                    callback.onSuccess(domainList);
                } else {
                    callback.onError("Erreur recalcul : " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TripDto> call, Throwable t) {
                callback.onError("Erreur réseau : " + t.getMessage());
            }
        });
    }
}