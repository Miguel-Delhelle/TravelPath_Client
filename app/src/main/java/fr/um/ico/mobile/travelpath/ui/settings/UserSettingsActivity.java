package fr.um.ico.mobile.travelpath.ui.settings;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.Data.Local.UserPrefsManager;
import fr.um.ico.mobile.travelpath.databinding.ActivityUserBinding; // Généré

public class UserSettingsActivity extends AppCompatActivity {

    private ActivityUserBinding binding;
    private UserPrefsManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefsManager = new UserPrefsManager(this);

        loadUI();

        binding.btnSave.setOnClickListener(v -> {
            saveData();
            Toast.makeText(this, "Préférences enregistrées", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void loadUI() {
        UserPreferencesDto prefs = prefsManager.loadPreferences();

        binding.etBudget.setText(String.valueOf(prefs.getDailyBudget()));
        binding.switchVehicle.setChecked(prefsManager.hasVehicle());

        binding.seekCulture.setProgress((int)(prefs.getScore("CULTURE") * 100));
        binding.seekNature.setProgress((int)(prefs.getScore("NATURE") * 100));
        binding.seekFood.setProgress((int)(prefs.getScore("FOOD") * 100));
        binding.seekFun.setProgress((int)(prefs.getScore("FUN") * 100));
        binding.seekNight.setProgress((int)(prefs.getScore("NIGHT_LIFE") * 100));
        binding.seekShopping.setProgress((int)(prefs.getScore("SHOPPING") * 100));

        if (prefs.getSpreadingScale() != null) {
            binding.seekSpreading.setProgress((int)(prefs.getSpreadingScale() * 100));
        }
    }

    private void saveData() {
        Map<String, Double> interests = new HashMap<>();
        interests.put("CULTURE", binding.seekCulture.getProgress() / 100.0);
        interests.put("NATURE", binding.seekNature.getProgress() / 100.0);
        interests.put("FOOD", binding.seekFood.getProgress() / 100.0);
        interests.put("FUN", binding.seekFun.getProgress() / 100.0);
        interests.put("NIGHT_LIFE", binding.seekNight.getProgress() / 100.0);
        interests.put("SHOPPING", binding.seekShopping.getProgress() / 100.0);

        Double budget;
        try {
            budget = Double.parseDouble(binding.etBudget.getText().toString());
        } catch (NumberFormatException e) {
            budget = 50.0;
        }

        Double spreading = binding.seekSpreading.getProgress() / 100.0;

        UserPreferencesDto dto = new UserPreferencesDto(interests, null, budget, spreading);
        prefsManager.savePreferences(dto, binding.switchVehicle.isChecked());
    }
}