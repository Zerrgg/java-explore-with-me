package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.enums.EventSortType;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.mapper.LocationMapper;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.location.repository.LocationRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.ewm.dto.GlobalConstants.DT_FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final StatsClient statsClient;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    private Integer getCountUniqueViews(HttpServletRequest request) {
        String[] uris = new String[]{request.getRequestURI()};

        List<ViewStatsDto> response = statsClient.getStats(
                LocalDateTime.now().minusYears(100).format(DT_FORMATTER),
                LocalDateTime.now().plusHours(1).format(DT_FORMATTER),
                true,
                uris);
        return response.size();

    }

    @Override
    public List<EventFullDto> getEventsByAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                               LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        log.info("Вывод событий на запрос администратора");

        checkStartIsBeforeEnd(rangeStart, rangeEnd);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Event> events = eventRepository.getEventsByAdmin(users, states, categories, getRangeStart(rangeStart), pageRequest);

        if (rangeEnd != null) {
            events = getEventsBeforeRangeEnd(events, rangeEnd);
        }

        return events.stream().map(EventMapper.INSTANCE::toEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("Обновление события с id {} по запросу администратора с параметрами {}", eventId, updateEventAdminRequest);

        checkNewEventDate(updateEventAdminRequest.getEventDate(), LocalDateTime.now().plusHours(1));

        Event event = checkEvent(eventId);

        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }

        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }

        if (updateEventAdminRequest.getCategoryId() != null) {
            Category category = checkCategory(updateEventAdminRequest.getCategoryId());
            event.setCategory(category);
        }

        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }

        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }

        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(getOrSaveLocation(updateEventAdminRequest.getLocation()));
        }

        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }

        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case REJECT_EVENT:
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        log.warn("Нельзя отклонить опубликованное событие.");
                        throw new ConflictException("Нельзя отклонить опубликованное событие.");
                    }
                    event.setState(EventState.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    if (event.getState() != (EventState.PENDING)) {
                        log.warn("Событие должно быть в состоянии ожидания публикации.");
                        throw new ConflictException("Событие должно быть в состоянии ожидания публикации.");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
            }
        }

        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }

        return EventMapper.INSTANCE.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getAllEventsByPrivate(Long userId, Pageable pageable) {
        log.info("Вывод всех событий пользователя с id {}", userId);
        checkUser(userId);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return events.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto addEventByPrivate(Long userId, NewEventDto newEventDto) {
        log.info("Создание нового события пользователем с id {} и параметрами {}", userId, newEventDto);

        checkNewEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        User initiator = checkUser(userId);
        Category eventCategory = checkCategory(newEventDto.getCategory());
        Location eventLocation = getOrSaveLocation(newEventDto.getLocation());
        Integer confirmedRequests = 0;
        Event newEvent = eventRepository.save(EventMapper.INSTANCE.toEvent(newEventDto, eventCategory,
                initiator, EventState.PENDING, eventLocation, confirmedRequests));


        return EventMapper.INSTANCE.toEventFullDto(newEvent);
    }

    @Override
    public EventFullDto getEventByPrivate(Long userId, Long eventId) {
        log.info("Вывод события по id {}, созданного пользователем с id {}", eventId, userId);
        checkUser(userId);
        Event event = checkEvent(eventId);
        return EventMapper.INSTANCE.toEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByPrivate(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        log.info("Обновление события с id {} по запросу пользователя с id {} с новыми параметрами {}",
                eventId, userId, updateEventUserRequest);

        Event event = checkEvent(eventId);

        checkUser(userId);

        checkUserIsOwner(event.getInitiator().getId(), userId);

        checkNewEventDate(updateEventUserRequest.getEventDate(), LocalDateTime.now().plusHours(2));

        if (event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Изменять можно только неопубликованные или отмененные события.");
            throw new ConflictException("Изменять можно только неопубликованные или отмененные события.");
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }

        if (updateEventUserRequest.getCategoryId() != null) {
            Category category = checkCategory(updateEventUserRequest.getCategoryId());
            event.setCategory(category);
        }

        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }

        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getLocation() != null) {
            Location location = getOrSaveLocation(updateEventUserRequest.getLocation());
            event.setLocation(location);
        }

        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }

        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }

        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }

        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        return EventMapper.INSTANCE.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getListEventsByPublic(
            String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Boolean onlyAvailable, EventSortType sort, Integer from, Integer size, HttpServletRequest request) {
        log.info("Вывод событий на публичный запрос с параметрами");

        checkStartIsBeforeEnd(rangeStart, rangeEnd);

        String sorting;
        if (sort == (EventSortType.EVENT_DATE)) {
            sorting = "eventDate";
        } else if (sort == (EventSortType.VIEWS)) {
            sorting = "views";
        } else {
            sorting = "id";
        }


        PageRequest pageRequest = PageRequest.of(from, size, Sort.by(sorting));
        List<Event> events = eventRepository.getEventsSort(text, EventState.PUBLISHED, categories, paid, getRangeStart(rangeStart), pageRequest);

        if (rangeEnd == null) rangeEnd = LocalDateTime.now().plusYears(1);

        events = getEventsBeforeRangeEnd(events, rangeEnd);

        if (events.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> eventsParticipantLimit = new HashMap<>();
        events.forEach(event -> eventsParticipantLimit.put(event.getId(), event.getParticipantLimit()));

        if (onlyAvailable) {
            events.stream()
                    .filter(eventShort -> (eventsParticipantLimit.get(eventShort.getId()) == 0 ||
                            eventsParticipantLimit.get(eventShort.getId()) > eventShort.getConfirmedRequests()))
                    .collect(Collectors.toList());
        }

        statsClient.addHit(EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DT_FORMATTER))
                .build());

        return events.stream()
                .map(EventMapper.INSTANCE::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByPublic(Long eventId, HttpServletRequest request) {
        log.info("Вывод события с id {} на публичный запрос", eventId);

        Event exsistEvent = checkEvent(eventId);

        if (!exsistEvent.getState().equals(EventState.PUBLISHED)) {
            log.warn("Событие с таким id не опубликовано.");
            throw new NotFoundException("Событие с таким id не опубликовано.");
        }

        Integer oldCountHit = getCountUniqueViews(request);

        statsClient.addHit(EndpointHitDto.builder()
                .app("main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DT_FORMATTER))
                .build());

        Integer newCountHit = getCountUniqueViews(request);

        if (newCountHit > oldCountHit) {
            exsistEvent.setViews(exsistEvent.getViews() + 1);
            eventRepository.save(exsistEvent);
        }

        return EventMapper.INSTANCE.toEventFullDto(exsistEvent);

    }

    @Override
    public List<Event> getEventsByIds(List<Long> eventsId) {
        log.info("Вывод списка событий с ids {}", eventsId);

        if (eventsId.isEmpty()) {
            return new ArrayList<>();
        }

        return eventRepository.findAllByIdIn(eventsId);
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
                    log.warn("Не найдено событие с id={}: ", eventId);
                    return new NotFoundException(String.format(
                            "Не найдено событие с id=%d", eventId));
                }
        );
    }

    private Location getOrSaveLocation(LocationDto locationDto) {
        Location newLocation = LocationMapper.toLocation(locationDto);
        return locationRepository.findByLatAndLon(newLocation.getLat(), newLocation.getLon())
                .orElseGet(() -> locationRepository.save(newLocation));
    }

    private void checkStartIsBeforeEnd(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException(String.format("Field: eventDate. Error: некорректные параметры временного " +
                    "интервала. Value: rangeStart = %s, rangeEnd = %s", rangeStart, rangeEnd));
        }
    }

    private void checkNewEventDate(LocalDateTime newEventDate, LocalDateTime minTimeBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(minTimeBeforeEventStart)) {
            throw new BadRequestException(String.format("Field: eventDate. Error: остается слишком мало времени для " +
                    "подготовки. Value: %s", newEventDate));
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Не найден пользователь с id={}: ", userId);
            return new NotFoundException(String.format(
                    "Не найден пользователь с id=%d", userId));
        });
    }

    private Category checkCategory(Long catId) {
        return categoryRepository.findById(catId).orElseThrow(() -> {
            log.warn("Не найден пользователь с id={}: ", catId);
            return new NotFoundException(String.format(
                    "Категории с таким id = %d не существует.", catId));
        });
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!id.equals(userId)) {
            log.warn("Пользователь не является инициатором.");
            throw new ConflictException("Пользователь не является инициатором.");
        }
    }

    private LocalDateTime getRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) return LocalDateTime.now();
        return rangeStart;
    }

    private List<Event> getEventsBeforeRangeEnd(List<Event> events, LocalDateTime rangeEnd) {
        return events.stream().filter(event -> event.getEventDate().isBefore(rangeEnd)).collect(Collectors.toList());
    }
}
