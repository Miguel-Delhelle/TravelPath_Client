package fr.um.ico.mobile.travelpath.ui.trip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

// --- Imports Clean Architecture ---
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.domain.repository.ITripRepository;

// --- Imports Data / DTOs ---
import fr.um.ico.mobile.travelpath.Data.Api.Dto.FactoryDto; // Indispensable
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.Data.Local.UserPrefsManager;

public class TripViewModel extends ViewModel {

    private final ITripRepository repository;
    private final UserPrefsManager prefsManager;

    // On utilise PointOfInterest (Domain) pour l'UI
    private final MutableLiveData<List<PointOfInterest>> route = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    private MoodRequestDto lastRequest;
    private List<PointOfInterest> currentPath;

    public TripViewModel(ITripRepository repository, UserPrefsManager prefsManager) {
        this.repository = repository;
        this.prefsManager = prefsManager;
    }

    public LiveData<List<PointOfInterest>> getRoute() { return route; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void searchTrip(double lat, double lon) {
        isLoading.setValue(true);
        UserPreferencesDto userProfile = prefsManager.loadPreferences();

        lastRequest = new MoodRequestDto(
                lat, lon, "ACTIVE", "SUNNY",
                LocalTime.now().toString().substring(0, 8),
                userProfile
        );

        repository.searchByMood(lastRequest, new ITripRepository.Callback<List<PointOfInterest>>() {
            @Override
            public void onSuccess(List<PointOfInterest> result) {
                currentPath = result;
                route.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }

    public void recalculateWithBanned(long bannedId) {
        if (lastRequest == null || currentPath == null) return;

        isLoading.setValue(true);

        // 1. Conversion Domain (PointOfInterest) -> DTO (PoiDto) via Factory
        // Nécessaire pour renvoyer les données au serveur
        List<PoiDto> pathDtos = currentPath.stream()
                .map(FactoryDto::from) // Utilisation propre de la Factory
                .collect(Collectors.toList());

        PoiDto bannedDto = new PoiDto();
        bannedDto.setId(bannedId);

        TripDto tripDto = new TripDto(pathDtos, bannedDto, lastRequest);

        repository.recalculateTrip(tripDto, new ITripRepository.Callback<List<PointOfInterest>>() {
            @Override
            public void onSuccess(List<PointOfInterest> result) {
                currentPath = result;
                route.setValue(result);
                isLoading.setValue(false);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
                isLoading.setValue(false);
            }
        });
    }
}