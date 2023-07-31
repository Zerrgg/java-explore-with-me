package ru.practicum.ewm.location.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;


@UtilityClass
public class LocationMapper {

    public Location toLocation(LocationDto locationDto){
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
