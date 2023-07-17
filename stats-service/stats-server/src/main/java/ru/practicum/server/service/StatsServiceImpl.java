package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.server.mapper.StatsMapper;
import ru.practicum.server.model.ViewStats;
import ru.practicum.server.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.dto.GlobalConstants.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;


    @Override
    @Transactional
    public void addHit(EndpointHitDto endpointHit) {
        statsRepository.save(statsMapper.toStats(endpointHit,
                LocalDateTime.parse(endpointHit.getTimestamp(), DT_FORMATTER)));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<ViewStats> list;

        if (uris == null || uris.isEmpty()) {
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
        return list.stream().map(statsMapper::toViewStatsDto).collect(Collectors.toList());
    }
}
