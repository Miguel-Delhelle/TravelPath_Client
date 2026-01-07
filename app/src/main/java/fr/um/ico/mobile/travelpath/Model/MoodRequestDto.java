package fr.um.ico.mobile.travelpath.Model;

public class MoodRequestDto {
    private Double lat;
    private Double lon;
    private String mood;       // "PARTY", "ACTIVE", etc.
    private String weather;    // "SUNNY", "RAINY", etc.
    private String localTime;  // "20:30:00"

    // L'ajout majeur : Le profil utilisateur
    private UserPreferencesDto userPreferences;

    public MoodRequestDto(Double lat, Double lon, String mood, String weather, String localTime, UserPreferencesDto userPreferences) {
        this.lat = lat;
        this.lon = lon;
        this.mood = mood;
        this.weather = weather;
        this.localTime = localTime;
        this.userPreferences = userPreferences;
    }

    // Getters / Setters...
}