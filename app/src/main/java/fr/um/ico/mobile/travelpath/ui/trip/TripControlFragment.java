package fr.um.ico.mobile.travelpath.ui.trip;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.ui.main.MainViewModelFactory;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.TravelPathApplication;
import fr.um.ico.mobile.travelpath.domain.Utils.LocationHelper;
import fr.um.ico.mobile.travelpath.databinding.FragmentBottomBarBinding;

public class TripControlFragment extends Fragment {

    private FragmentBottomBarBinding binding;
    private TripViewModel viewModel;
    private LocationHelper locationHelper;
    private MapUpdateListener mapUpdateListener;

    public interface MapUpdateListener {
        void onRouteFound(List<PointOfInterest> pois, double userLat, double userLon); // TYPE CORRIGÉ
        void onUserOptionClicked();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MapUpdateListener) mapUpdateListener = (MapUpdateListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationHelper = new LocationHelper(requireActivity());

        // --- INJECTION MANUELLE DU VIEWMODEL ---
        TravelPathApplication app = (TravelPathApplication) requireActivity().getApplication();
        MainViewModelFactory factory = new MainViewModelFactory(
                app.appContainer.getTripRepository(),
                app.appContainer.getUserPrefsManager()
        );
        viewModel = new ViewModelProvider(this, factory).get(TripViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Initialisation ViewBinding
        binding = FragmentBottomBarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Observer
        viewModel.getRoute().observe(getViewLifecycleOwner(), route -> {
            if (mapUpdateListener != null) {
                locationHelper.getLastLocation((lat, lon) ->
                        mapUpdateListener.onRouteFound(route, lat, lon)
                );
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.btnSearchPath.setText(isLoading ? "Chargement..." : "GO !");
            binding.btnSearchPath.setEnabled(!isLoading);
        });

        // 2. LISTENERS (Actions UI)
        binding.btnSearchPath.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Acquisition GPS...", Toast.LENGTH_SHORT).show();
            locationHelper.getLastLocation((lat, lon) -> viewModel.searchTrip(lat, lon));
        });

        binding.btnUserOptions.setOnClickListener(v -> {
            if (mapUpdateListener != null) mapUpdateListener.onUserOptionClicked();
        });

        binding.btnLeftPlaceholder.setOnClickListener(v ->
                Toast.makeText(getContext(), "Soon...", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    public void recalculateWithBanned(long bannedId) {
        viewModel.recalculateWithBanned(bannedId);
    }
}