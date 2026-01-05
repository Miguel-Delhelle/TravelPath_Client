package fr.um.ico.mobile.travelpath.Model;

import java.util.Map;

public class SearchRequestDto {
    private Double lat;
    private Double lon;

    // Correspond au Map<String, Double> du backend (ex: "CULTURE": 0.9)
    private Map<String, Double> affinityVector;

    public SearchRequestDto(Double lat, Double lon, Map<String, Double> affinityVector) {
        this.lat = lat;
        this.lon = lon;
        this.affinityVector = affinityVector;
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

    public Map<String, Double> getAffinityVector() {
        return affinityVector;
    }

    public void setAffinityVector(Map<String, Double> affinityVector) {
        this.affinityVector = affinityVector;
    }
}