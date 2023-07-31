package ru.practicum.ewm.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.model.Location;


@Component
@Mapper(componentModel = "spring")
public interface LocationMapper {
//    @Mapping(target = "id", ignore = true)
//    Location toLocation(LocationDto locationDto);

    LocationDto toLocationDto(Location location);

    default Location toLocation(LocationDto locationDto){
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }
}
