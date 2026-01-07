package fr.um.ico.mobile.travelpath.Model;

import java.util.HashMap;
import java.util.Map;

public class UserPreferencesDto {
    private Map<String, Double> interests;
    private Double searchRadius; // En mètres
    private Double dailyBudget;  // En Euros

    private Double spreadingScale; // 0.0 = Dense, 1.0 = Aéré

    // 1. Constructeur vide (Indispensable pour Gson/Retrofit)
    public UserPreferencesDto() {
        this.interests = new HashMap<>();
    }

    public UserPreferencesDto(Map<String, Double> interests, Double searchRadius, Double dailyBudget, Double spreadingScale) {
        this.interests = interests != null ? interests : new HashMap<>();
        this.searchRadius = searchRadius;
        this.dailyBudget = dailyBudget;
        this.spreadingScale = spreadingScale;
    }

    // 2. Constructeur complet (Utilisé par UserPrefsManager)
//    public UserPreferencesDto(Map<String, Double> interests, Double searchRadius, Double dailyBudget) {
//        this.interests = interests != null ? interests : new HashMap<>();
//        this.searchRadius = searchRadius;
//        this.dailyBudget = dailyBudget;
//    }

    // 3. Constructeur simple (Compatibilité)
//    public UserPreferencesDto(Map<String, Double> interests) {
//        this(interests, null, null);
//    }

    // --- Getters & Setters ---

    public Map<String, Double> getInterests() {
        return interests;
    }

    public void setInterests(Map<String, Double> interests) {
        this.interests = interests;
    }

    public Double getSearchRadius() {
        return searchRadius;
    }

    public void setSearchRadius(Double searchRadius) {
        this.searchRadius = searchRadius;
    }

    public Double getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(Double dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    // --- Helper Pratique ---
    // Permet de récupérer le score d'une catégorie sans crasher si elle n'existe pas
    // Utilisé dans UserActivity pour remettre les Sliders à la bonne position
    public double getScore(String category) {
        if (interests == null) return 0.5; // Valeur par défaut
        return interests.getOrDefault(category, 0.5);
    }

    public Double getSpreadingScale() { return spreadingScale; }
    public void setSpreadingScale(Double spreadingScale) { this.spreadingScale = spreadingScale; }
}