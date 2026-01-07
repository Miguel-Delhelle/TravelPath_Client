package fr.um.ico.mobile.travelpath.Data.Api.Dto.Models;

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