package fr.um.ico.mobile.travelpath.Main.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import fr.um.ico.mobile.travelpath.Main.Adapter.TripStepAdapter;
import fr.um.ico.mobile.travelpath.Model.PoiDto;
import fr.um.ico.mobile.travelpath.R;

public class TopBarFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
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
        View view = inflater.inflate(R.layout.fragment_top_bar, container, false);
        recyclerView = view.findViewById(R.id.rvTripSteps);
        tvEmpty = view.findViewById(R.id.tvEmptyState);

        setupRecyclerView();
        return view;
    }

    private void setupRecyclerView() {
        adapter = new TripStepAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        // GESTION DU SWIPE (Balayage)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.UP | ItemTouchHelper.DOWN) {
            // Note: Horizontal RecyclerView -> Swipe UP/DOWN pour supprimer c'est plus ergonomique,
            // ou LEFT/RIGHT si tu préfères mais ça conflit avec le scroll.
            // Mettons UP pour "jeter" le lieu.

            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                PoiDto item = adapter.getStepAt(position);
                Toast.makeText(
                        viewHolder.itemView.getContext(),
                        "ID : " + item.getId(),
                        Toast.LENGTH_SHORT
                ).show();
                // On prévient l'activité qu'il faut bannir ce lieu
                if (listener != null) {
                    listener.onStepSwiped(item.getId());
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Méthode appelée par MainActivity pour afficher les données
    public void updateSteps(List<PoiDto> pois) {
        if (pois == null || pois.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setSteps(pois);
        }
    }
}