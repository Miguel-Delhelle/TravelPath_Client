package fr.um.ico.mobile.travelpath.Model;
import java.util.List;

public class TripDto {
    private List<PoiDto> path;
    private PoiDto bannedPoi;
    private MoodRequestDto context; // On stocke la requête initiale ici

    public TripDto(List<PoiDto> path, PoiDto bannedPoi, MoodRequestDto context) {
        this.path = path;
        this.bannedPoi = bannedPoi;
        this.context = context;
    }

    public List<PoiDto> getPath() { return path; }
    public PoiDto getBannedPoi() { return bannedPoi; }
    public MoodRequestDto getContext() { return context; }
}