package fr.um.ico.mobile.travelpath.Model;

import java.util.List;

public class MoodRequestDto {
    private Double lat;
    private Double lon;
    private String mood;       // "PARTY", "ACTIVE", etc.
    private String weather;    // "SUNNY", "RAINY", etc.
    private String localTime;  // "20:30:00"

    // L'ajout majeur : Le profil utilisateur
    private UserPreferencesDto userPreferences;

    private List<Long> blacklistedIds;

    public List<Long> getBlacklistedIds() { return blacklistedIds; }
    public void setBlacklistedIds(List<Long> blacklistedIds) { this.blacklistedIds = blacklistedIds; }

    public MoodRequestDto(Double lat, Double lon, String mood, String weather, String localTime, UserPreferencesDto userPreferences) {
        this.lat = lat;
        this.lon = lon;
        this.mood = mood;
        this.weather = weather;
        this.localTime = localTime;
        this.userPreferences = userPreferences;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocalTime() {
        return localTime;
    }

    public void setLocalTime(String localTime) {
        this.localTime = localTime;
    }

    public UserPreferencesDto getUserPreferences() {
        return userPreferences;
    }

    public void setUserPreferences(UserPreferencesDto userPreferences) {
        this.userPreferences = userPreferences;
    }

    // Getters / Setters...
}