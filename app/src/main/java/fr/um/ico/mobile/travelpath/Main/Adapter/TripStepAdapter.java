package fr.um.ico.mobile.travelpath.Main.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.R;

public class TripStepAdapter extends RecyclerView.Adapter<TripStepAdapter.StepViewHolder> {

    private List<PoiDto> steps = new ArrayList<>();

    public void setSteps(List<PoiDto> newSteps) {
        this.steps = newSteps;
        notifyDataSetChanged();
    }

    public PoiDto getStepAt(int position) {
        return steps.get(position);
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_step, parent, false);
        return new StepViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        PoiDto step = steps.get(position);

        holder.tvName.setText(step.getName());
        holder.tvNumber.setText(String.valueOf(position + 1));

        // Affichage combiné propre
        String subText = step.getCategory() + " • " + step.getEstimatedCost() + "€";
        holder.tvDistance.setText(subText);
    }

    @Override
    public int getItemCount() { return steps.size(); }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvNumber, tvDistance;
        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvPoiName);
            tvNumber = itemView.findViewById(R.id.tvStepNumber);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }
    }
}