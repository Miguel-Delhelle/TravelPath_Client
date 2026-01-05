package fr.um.ico.mobile.travelpath.Model;

public class PoiDto {
    private Long id;
    private String name;
    private Double lat;
    private Double lon;
    private String category; // ex: "MUSEUM", "PARK"

    private Double estimatedCost; // Ajouté

    // Constructeur vide nécessaire pour Gson (parfois)
    public PoiDto() {
    }

    public PoiDto(String name, Double lat, Double lon, String category) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public String getCategory() {
        return category;
    }

    public Double getEstimatedCost() { return estimatedCost; }

    // Tu peux ajouter des setters si besoin
}