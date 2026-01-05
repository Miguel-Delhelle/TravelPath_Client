package fr.um.ico.mobile.travelpath.Model;

public class ContextualSearchDto {
    private Double lat;
    private Double lon;
    private String mood;       // "ACTIVE", "CHILL", etc.
    private String weather;    // "SUNNY", "RAINY", etc.
    private String localTime;  // "15:00:00"

    public ContextualSearchDto(Double lat, Double lon, String mood, String weather, String localTime) {
        this.lat = lat;
        this.lon = lon;
        this.mood = mood;
        this.weather = weather;
        this.localTime = localTime;
    }

    // Getters et Setters nécessaires pour Gson
    public Double getLat() { return lat; }
    public Double getLon() { return lon; }
    public String getMood() { return mood; }
    public String getWeather() { return weather; }
    public String getLocalTime() { return localTime; }
}