package fr.um.ico.mobile.travelpath.Model;

import java.util.HashMap;
import java.util.Map;

public class UserPreferencesDto {
    private Map<String, Double> interests;

    public UserPreferencesDto(Map<String, Double> interests) {
        this.interests = interests;
    }

    // Constructeur vide et getters/setters nécessaires pour Gson
    public UserPreferencesDto() {
        this.interests = new HashMap<>();
    }

    public Map<String, Double> getInterests() { return interests; }
    public void setInterests(Map<String, Double> interests) { this.interests = interests; }
}