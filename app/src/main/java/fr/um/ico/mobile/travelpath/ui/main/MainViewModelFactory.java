package fr.um.ico.mobile.travelpath.ui.main;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import fr.um.ico.mobile.travelpath.Data.Repository.TripRepositoryImpl;
import fr.um.ico.mobile.travelpath.Data.Local.UserPrefsManager;
import fr.um.ico.mobile.travelpath.ui.trip.TripViewModel;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final TripRepositoryImpl tripRepository;
    private final UserPrefsManager userPrefsManager;

    public MainViewModelFactory(TripRepositoryImpl tripRepository, UserPrefsManager userPrefsManager) {
        this.tripRepository = tripRepository;
        this.userPrefsManager = userPrefsManager;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(TripViewModel.class)) {
            return (T) new TripViewModel(tripRepository, userPrefsManager);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}