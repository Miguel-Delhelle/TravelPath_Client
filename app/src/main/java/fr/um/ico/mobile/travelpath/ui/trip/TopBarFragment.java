package fr.um.ico.mobile.travelpath.ui.trip;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fr.um.ico.mobile.travelpath.ui.trip.adapter.TripStepAdapter;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest; // IMPORT DU DOMAINE
import fr.um.ico.mobile.travelpath.databinding.FragmentTopBarBinding;

public class TopBarFragment extends Fragment {

    private FragmentTopBarBinding binding;
    private TripStepAdapter adapter;
    private OnStepActionListener listener;

    public interface OnStepActionListener {
        void onStepSwiped(long osmIdToBan);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnStepActionListener) listener = (OnStepActionListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTopBarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupToggleLogic();
    }

    private void setupToggleLogic() {
        binding.btnExpand.setOnClickListener(v -> {
            binding.layoutExpanded.setVisibility(View.VISIBLE);
            binding.cardCollapsed.setVisibility(View.GONE);
        });

        binding.btnClose.setOnClickListener(v -> {
            binding.layoutExpanded.setVisibility(View.GONE);
            binding.cardCollapsed.setVisibility(View.VISIBLE);
        });
    }

    private void setupRecyclerView() {
        adapter = new TripStepAdapter();
        binding.rvTripSteps.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTripSteps.setAdapter(adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) { return false; }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                PointOfInterest item = adapter.getStepAt(position); // TYPE CORRIGÉ

                if (item != null && item.getId() != null) {
                    if (listener != null) listener.onStepSwiped(item.getId());
                } else {
                    adapter.notifyItemChanged(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(binding.rvTripSteps);
    }

    public void updateSteps(List<PointOfInterest> pois) {
        if (pois == null || pois.isEmpty()) {
            binding.tvNextStepName.setText("Aucun trajet");
            return;
        }

        PointOfInterest nextStep = pois.get(0);
        binding.tvNextStepName.setText(nextStep.getName());
        binding.tvNextStepInfo.setText(nextStep.getCategory() + " • " + nextStep.getEstimatedCost() + "€");

        adapter.setSteps(pois);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}