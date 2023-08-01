package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.ParticipationRequestService;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class ParticipationRequestController {
    private final ParticipationRequestService participationRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addEventRequest(@PathVariable Long userId,
                                                   @RequestParam Long eventId) {
        log.info("POST запрос от пользователя с id = {} на добавление заявки на участие в событии по id = {}", userId, eventId);
        return participationRequestService.addEventRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelEventRequest(@PathVariable Long userId,
                                                      @PathVariable Long requestId) {
        log.info("PATCH запрос от пользователя с id = {} на отмену запроса на участие по id = {}", userId, requestId);
        return participationRequestService.cancelEventRequest(userId, requestId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByRequester(@PathVariable Long userId) {
        log.info("GET запрос на получение заявок от пользователя с id = {} на участие в событиях.", userId);
        return participationRequestService.getEventRequestsByRequester(userId);
    }
}
