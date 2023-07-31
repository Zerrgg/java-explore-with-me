package ru.practicum.ewm.server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.dto.GlobalConstants.*;

@Validated
@RestController
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody EndpointHitDto endpointHit) {
        statsService.addHit(endpointHit);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = DT_FORMAT) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = DT_FORMAT) LocalDateTime end,
                                    @RequestParam(required = false) String[] uris,
                                    @RequestParam(defaultValue = "false") Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Недопустимый временной промежуток.");
        }
        return statsService.getStats(start, end, uris, unique);
    }
}
