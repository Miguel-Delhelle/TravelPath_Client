package fr.um.ico.mobile.travelpath.Data.Api.Dto;

import fr.um.ico.mobile.travelpath.Data.Api.Dto.Models.PoiDto;
import fr.um.ico.mobile.travelpath.domain.model.PointOfInterest;

public class FactoryDto {

    public static PoiDto from(PointOfInterest pointOfInterest){
        return new PoiDto(pointOfInterest.getId(),
                pointOfInterest.getName(),
                pointOfInterest.getLat(),
                pointOfInterest.getLon(),
                pointOfInterest.getCategory(),
                pointOfInterest.getEstimatedCost());
    }

    public static PointOfInterest from(PoiDto poiDto){
        return new PointOfInterest(poiDto.getId(),
                poiDto.getName(),
                poiDto.getLat(),
                poiDto.getLon(),
                poiDto.getCategory(),
                poiDto.getEstimatedCost());
    }
}
