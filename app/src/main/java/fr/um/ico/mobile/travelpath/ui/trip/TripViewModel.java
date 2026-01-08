package fr.um.ico.mobile.travelpath.ui.trip;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.domain.repository.ITripRepository;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.FactoryDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.MoodRequestDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.TripDto;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.Data.Local.UserPrefsManager;

public class TripViewModel extends ViewModel {

    private final ITripRepository repository;
    private final UserPrefsManager prefsManager;

    private final MutableLiveData<List<PointOfInterest>> route = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // C'est cette variable qui garde la mémoire de l'état (Blacklist etc.)
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

        // 1. On prépare la liste du chemin actuel
        List<PoiDto> pathDtos = currentPath.stream()
                .map(FactoryDto::from)
                .collect(Collectors.toList());

        // 2. CORRECTION ICI : On retrouve l'objet complet dans la liste pour avoir ses coordonnées
        PoiDto bannedDto = currentPath.stream()
                .filter(poi -> poi.getId().equals(bannedId)) // On cherche celui qui a l'ID
                .findFirst()
                .map(FactoryDto::from) // On le convertit en DTO complet (avec lat/lon)
                .orElse(null);

        if (bannedDto == null) {
            bannedDto = new PoiDto();
            bannedDto.setId(bannedId);
        }

        TripDto tripDto = new TripDto(pathDtos, bannedDto, lastRequest);

        // ATTENTION : Le Callback attend maintenant un TripDto complet
        repository.recalculateTrip(tripDto, new ITripRepository.Callback<TripDto>() {
            @Override
            public void onSuccess(TripDto result) {
                // 1. MISE A JOUR DU CONTEXTE (CRUCIAL !!)
                // Le serveur nous renvoie le contexte avec la blacklist augmentée [A, B...]
                // On l'enregistre pour la prochaine fois.
                if (result.getContext() != null) {
                    lastRequest = result.getContext();
                }

                // 2. Mise à jour de l'UI
                List<PointOfInterest> newPathDomain = result.getPath().stream()
                        .map(FactoryDto::from)
                        .collect(Collectors.toList());

                currentPath = newPathDomain;
                route.setValue(newPathDomain);

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