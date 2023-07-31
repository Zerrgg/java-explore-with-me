package ru.practicum.ewm.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventSortType;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface EventService {

    List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                        LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<EventShortDto> getAllEventsByPrivate(Long userId, Pageable pageable);

    EventFullDto addEventByPrivate(Long userId, NewEventDto newEventDto);

    EventFullDto getEventByPrivate(Long userId, Long eventId);

    EventFullDto updateEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<EventShortDto> getListEventsByPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, EventSortType sort,
                                              Integer from, Integer size, HttpServletRequest request);

    EventFullDto getEventByPublic(Long id, HttpServletRequest request);

    List<Event> getEventsByIds(List<Long> eventsId);

}
