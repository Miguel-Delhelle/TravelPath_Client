package fr.um.ico.mobile.travelpath.domain.repository;

import java.util.List;

import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripDto;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;

public interface ITripRepository {

    interface Callback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    void searchByMood(MoodRequestDto request, Callback<List<PointOfInterest>> callback);
    void recalculateTrip(TripDto tripDto, Callback<List<PointOfInterest>> callback);
}

