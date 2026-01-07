package fr.um.ico.mobile.travelpath.Model;

public class PoiDto {
    private Long id;
    private String name;
    private Double lat;
    private Double lon;
    private String category;      // ex: "MUSEUM", "PARK", "NIGHT_LIFE"
    private Double estimatedCost; // ex: 10.0, 0.0

    // Constructeur vide (Indispensable pour Retrofit/Gson)
    public PoiDto() {
    }

    // Constructeur complet (Utilisé si tu dois créer des objets manuellement)
    public PoiDto(Long id, String name, Double lat, Double lon, String category, Double estimatedCost) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.category = category;
        this.estimatedCost = estimatedCost;
    }

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public Double getEstimatedCost() {
        return estimatedCost;
    }
    public void setEstimatedCost(Double estimatedCost) {
        this.estimatedCost = estimatedCost;
    }
}