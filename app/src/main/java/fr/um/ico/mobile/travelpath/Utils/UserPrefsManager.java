package fr.um.ico.mobile.travelpath.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

import fr.um.ico.mobile.travelpath.Model.UserPreferencesDto;

public class UserPrefsManager {
    private static final String PREF_NAME = "TravelPath_User_Prefs";
    private final SharedPreferences prefs;

    public UserPrefsManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void savePreferences(UserPreferencesDto dto, boolean hasVehicle) {
        SharedPreferences.Editor editor = prefs.edit();

        // 1. Les Intérêts (0.0 à 1.0)
        Map<String, Double> interests = dto.getInterests();
        editor.putFloat("score_culture", interests.getOrDefault("CULTURE", 0.5).floatValue());
        editor.putFloat("score_nature", interests.getOrDefault("NATURE", 0.5).floatValue());
        editor.putFloat("score_night", interests.getOrDefault("NIGHT_LIFE", 0.5).floatValue());
        editor.putFloat("score_food", interests.getOrDefault("FOOD", 0.5).floatValue());
        editor.putFloat("score_shopping", interests.getOrDefault("SHOPPING", 0.5).floatValue());
        editor.putFloat("score_fun", interests.getOrDefault("FUN", 0.5).floatValue());

        // 2. Budget & Véhicule
        editor.putFloat("daily_budget", dto.getDailyBudget().floatValue());
        editor.putBoolean("has_vehicle", hasVehicle);

        editor.putFloat("spreading_scale", dto.getSpreadingScale() != null ? dto.getSpreadingScale().floatValue() : 0.5f);

        editor.apply();

        editor.apply();
    }

    public UserPreferencesDto loadPreferences() {
        Map<String, Double> interests = new HashMap<>();
        interests.put("CULTURE", (double) prefs.getFloat("score_culture", 0.5f));
        interests.put("NATURE", (double) prefs.getFloat("score_nature", 0.5f));
        interests.put("NIGHT_LIFE", (double) prefs.getFloat("score_night", 0.2f));
        interests.put("FOOD", (double) prefs.getFloat("score_food", 0.5f));
        interests.put("SHOPPING", (double) prefs.getFloat("score_shopping", 0.3f));
        interests.put("FUN", (double) prefs.getFloat("score_fun", 0.5f));

        double budget = prefs.getFloat("daily_budget", 50.0f);
        boolean hasVehicle = prefs.getBoolean("has_vehicle", false);

        double spreading = prefs.getFloat("spreading_scale", 0.5f);

        double radius = hasVehicle ? 30000.0 : 3000.0;

        return new UserPreferencesDto(interests, radius, budget, spreading);
    }

    // Helper pour l'UI (pour savoir si le switch doit être ON ou OFF)
    public boolean hasVehicle() {
        return prefs.getBoolean("has_vehicle", false);
    }
}