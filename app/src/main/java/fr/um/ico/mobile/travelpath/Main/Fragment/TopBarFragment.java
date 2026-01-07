package fr.um.ico.mobile.travelpath.Main.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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
    private TripStepAdapter adapter;
    private OnStepActionListener listener;

    // Vues pour le Toggle
    private View layoutExpanded;
    private View cardCollapsed;
    private TextView tvNextName, tvNextInfo;
    private ImageButton btnExpand, btnClose;

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

        // Binding
        recyclerView = view.findViewById(R.id.rvTripSteps);
        layoutExpanded = view.findViewById(R.id.layout_expanded);
        cardCollapsed = view.findViewById(R.id.card_collapsed);
        tvNextName = view.findViewById(R.id.tvNextStepName);
        tvNextInfo = view.findViewById(R.id.tvNextStepInfo);
        btnExpand = view.findViewById(R.id.btnExpand);
        btnClose = view.findViewById(R.id.btnClose);

        setupRecyclerView();
        setupToggleLogic();

        return view;
    }

    private void setupToggleLogic() {
        // Ouvrir
        btnExpand.setOnClickListener(v -> {
            layoutExpanded.setVisibility(View.VISIBLE);
            cardCollapsed.setVisibility(View.GONE);
        });

        // Fermer
        btnClose.setOnClickListener(v -> {
            layoutExpanded.setVisibility(View.GONE);
            cardCollapsed.setVisibility(View.VISIBLE);
        });
    }

    private void setupRecyclerView() {
        adapter = new TripStepAdapter();
        // CHANGEMENT ICI : VERTICAL au lieu d'HORIZONTAL
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // Swipe (Maintenant Left/Right car liste verticale)
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                PoiDto item = adapter.getStepAt(position);

                if (item != null && item.getId() != null) {
                    if (listener != null) listener.onStepSwiped(item.getId());
                } else {
                    // Si pas d'ID, on annule visuellement le swipe
                    adapter.notifyItemChanged(position);
                }
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void updateSteps(List<PoiDto> pois) {
        if (pois == null || pois.isEmpty()) {
            tvNextName.setText("Aucun trajet");
            return;
        }

        // 1. Mettre à jour la vue "Réduite" (Prochaine étape)
        PoiDto nextStep = pois.get(0);
        tvNextName.setText(nextStep.getName());
        tvNextInfo.setText(nextStep.getCategory() + " • " + nextStep.getEstimatedCost() + "€");

        // 2. Mettre à jour la liste complète
        adapter.setSteps(pois);
    }
}