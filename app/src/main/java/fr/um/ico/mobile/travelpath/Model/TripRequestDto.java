package fr.um.ico.mobile.travelpath.Model;

public class TripRequestDto {
    private Double lat;
    private Double lon;
    private UserPreferencesDto preferences;

    public TripRequestDto(Double lat, Double lon, UserPreferencesDto preferences) {
        this.lat = lat;
        this.lon = lon;
        this.preferences = preferences;
    }
}