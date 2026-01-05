package fr.um.ico.mobile.travelpath.Main.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import fr.um.ico.mobile.travelpath.R;

public class BottomBarFragment extends Fragment {

    private OnApiRequestListener callback;

    // Interface que l'Activity DOIT implémenter
    public interface OnApiRequestListener {
        void onApiRequest();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnApiRequestListener) {
            callback = (OnApiRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnApiRequestListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_bar, container, false);

        view.findViewById(R.id.btn_search_path).setOnClickListener(v -> {
            if (callback != null) callback.onApiRequest();
        });

        return view;
    }
}