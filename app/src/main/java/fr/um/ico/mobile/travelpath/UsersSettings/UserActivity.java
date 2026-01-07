package fr.um.ico.mobile.travelpath.UsersSettings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Map;
import fr.um.ico.mobile.travelpath.Model.UserPreferencesDto;
import fr.um.ico.mobile.travelpath.R;
import fr.um.ico.mobile.travelpath.Utils.UserPrefsManager;

public class UserActivity extends AppCompatActivity {

    private EditText etBudget;
    private Switch switchVehicle; // Nom corrigé
    private SeekBar seekCulture, seekNature, seekFood, seekFun, seekNight, seekShopping;
    private UserPrefsManager prefsManager;

    private SeekBar seekSpreading; // Nouveau

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // 1. Toujours en premier
        setContentView(R.layout.activity_user); // 2. On charge le XML (INDISPENSABLE avant les findViewById)

        prefsManager = new UserPrefsManager(this);

        // 3. Binding (Liaison des variables Java aux IDs XML)

        // Inputs simples
        etBudget = findViewById(R.id.etBudget);
        switchVehicle = findViewById(R.id.switchVehicle);

        // Sliders des Intérêts
        seekCulture = findViewById(R.id.seekCulture);
        seekNature = findViewById(R.id.seekNature);
        seekFood = findViewById(R.id.seekFood);
        seekFun = findViewById(R.id.seekFun);
        seekNight = findViewById(R.id.seekNight);
        seekShopping = findViewById(R.id.seekShopping);

        // Nouveau Slider : Densité
        seekSpreading = findViewById(R.id.seekSpreading);

        // Bouton
        Button btnSave = findViewById(R.id.btnSave);

        // 4. Charger les données existantes dans l'interface
        loadUI();

        // 5. Gérer le clic sur Sauvegarder
        btnSave.setOnClickListener(v -> {
            saveData(); // Appelle ta méthode qui lit les sliders et sauvegarde
            Toast.makeText(this, "Préférences mises à jour", Toast.LENGTH_SHORT).show();
            finish(); // Ferme l'écran et revient à la map
        });
    }

    private void loadUI() {
        UserPreferencesDto prefs = prefsManager.loadPreferences();

        etBudget.setText(String.valueOf(prefs.getDailyBudget()));
        switchVehicle.setChecked(prefsManager.hasVehicle());

        seekCulture.setProgress((int)(prefs.getScore("CULTURE") * 100));
        seekNature.setProgress((int)(prefs.getScore("NATURE") * 100));
        seekFood.setProgress((int)(prefs.getScore("FOOD") * 100));
        seekFun.setProgress((int)(prefs.getScore("FUN") * 100));
        seekNight.setProgress((int)(prefs.getScore("NIGHT_LIFE") * 100));
        seekShopping.setProgress((int)(prefs.getScore("SHOPPING") * 100));
    }

    private void saveData() {
        Map<String, Double> interests = new HashMap<>();
        interests.put("CULTURE", seekCulture.getProgress() / 100.0);
        interests.put("NATURE", seekNature.getProgress() / 100.0);
        interests.put("FOOD", seekFood.getProgress() / 100.0);
        interests.put("FUN", seekFun.getProgress() / 100.0);
        interests.put("NIGHT_LIFE", seekNight.getProgress() / 100.0);
        interests.put("SHOPPING", seekShopping.getProgress() / 100.0);

        Double budget;
        try {
            budget = Double.parseDouble(etBudget.getText().toString());
        } catch (NumberFormatException e) {
            budget = 50.0;
        }
        Double spreading = seekSpreading.getProgress() / 100.0; // Valeur entre 0.0 et 1.0

        UserPreferencesDto dto = new UserPreferencesDto(interests, null, budget, spreading);
        prefsManager.savePreferences(dto, switchVehicle.isChecked());
    }
}