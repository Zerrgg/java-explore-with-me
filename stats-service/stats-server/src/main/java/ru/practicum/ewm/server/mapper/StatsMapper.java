package ru.practicum.ewm.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.model.Stats;
import ru.practicum.ewm.server.model.ViewStats;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Mapper(componentModel = "spring")
public interface StatsMapper {

    StatsMapper INSTANCE = Mappers.getMapper(StatsMapper.class);
    default Stats toStats(EndpointHitDto endpointHit) {
        return Stats.builder()
                .app(endpointHit.getApp())
                .timestamp(LocalDateTime.parse(endpointHit.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .build();
    }

    EndpointHitDto toEndpointHitDto(Stats stats);

    ViewStatsDto toViewStatsDto(ViewStats viewStats);
}