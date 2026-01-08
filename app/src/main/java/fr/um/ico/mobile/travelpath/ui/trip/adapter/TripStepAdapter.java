package fr.um.ico.mobile.travelpath.ui.trip.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;
import fr.um.ico.mobile.travelpath.databinding.ItemTripStepBinding;

public class TripStepAdapter extends RecyclerView.Adapter<TripStepAdapter.StepViewHolder> {

    private List<PointOfInterest> steps = new ArrayList<>();

    // --- 1. Interface de Clic ---
    private OnStepClickListener listener;

    public interface OnStepClickListener {
        void onStepClick(int position); // On renvoie l'index pour le retrouver sur la map
    }

    public void setOnStepClickListener(OnStepClickListener listener) {
        this.listener = listener;
    }

    public void setSteps(List<PointOfInterest> newSteps) {
        this.steps = newSteps;
        notifyDataSetChanged();
    }

    public PointOfInterest getStepAt(int position) {
        return steps.get(position);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTripStepBinding binding = ItemTripStepBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StepViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        PointOfInterest step = steps.get(position);
        holder.bind(step, position, listener);
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private final ItemTripStepBinding binding;

        public StepViewHolder(ItemTripStepBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PointOfInterest step, int position, OnStepClickListener listener) {
            binding.tvPoiName.setText(step.getName());
            binding.tvStepNumber.setText(String.valueOf(position + 1));
            String subText = step.getCategory() + " • " + step.getEstimatedCost() + "€";
            binding.tvDistance.setText(subText);

            // --- Clic sur l'item ---
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onStepClick(position);
            });
        }
    }
}