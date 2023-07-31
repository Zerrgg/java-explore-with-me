package ru.practicum.ewm.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.mapper.StatsMapper;
import ru.practicum.ewm.server.model.Stats;
import ru.practicum.ewm.server.model.ViewStats;
import ru.practicum.ewm.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public EndpointHitDto addHit(EndpointHitDto endpointHit) {
        Stats hit = statsRepository.save(StatsMapper.INSTANCE.toStats(endpointHit));
        return StatsMapper.INSTANCE.toEndpointHitDto(hit);
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        List<ViewStats> list;

        if (start == null || end == null || start.isAfter(end)) {
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }

        if (uris == null || uris.length == 0) {
            if (unique) {
                list = statsRepository.getAllStatsDistinctIp(start, end);
            } else {
                list = statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                list = statsRepository.getStatsByUrisDistinctIp(start, end, uris);
            } else {
                list = statsRepository.getStatsByUris(start, end, uris);
            }
        }
        return list.stream().map(StatsMapper.INSTANCE::toViewStatsDto).collect(Collectors.toList());
    }
}
