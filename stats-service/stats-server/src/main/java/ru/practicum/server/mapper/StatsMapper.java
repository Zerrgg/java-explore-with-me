package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.model.Stats;
import ru.practicum.server.model.ViewStats;

import java.time.LocalDateTime;

@Component
@Mapper(componentModel = "spring")
public interface StatsMapper {
    @Mapping(target = "timestamp", expression = "java(timestamp)")
    Stats toStats(EndpointHitDto endpointHit, LocalDateTime timestamp);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}