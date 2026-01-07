package fr.um.ico.mobile.travelpath.domain.model;

public class PointOfInterest {
    private final Long id;
    private final String name;
    private final double lat;
    private final double lon;
    private final String category;
    private final double estimatedCost;

    public PointOfInterest(Long id, String name, double lat, double lon, String category, double estimatedCost) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.estimatedCost = estimatedCost;
    }

    // Getters uniquement (Immuable = Robuste)
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public String getCategory() { return category; }
    public double getEstimatedCost() { return estimatedCost; }

}
