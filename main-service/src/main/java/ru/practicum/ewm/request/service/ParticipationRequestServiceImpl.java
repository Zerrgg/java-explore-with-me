package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.request.model.ParticipationRequest;
import ru.practicum.ewm.request.repository.ParticipationRequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addEventRequest(Long userId, Long eventId) {
        log.info("Добавление запроса от пользователя с id = {} на участие в событии по id = {}.", userId, eventId);
        User user = checkUser(userId);
        Event event = checkEvent(eventId);

        Optional<ParticipationRequest> oldRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);

        checkNewLimit(event.getConfirmedRequests(), event.getParticipantLimit());

        if (oldRequest.isPresent()) {
            log.warn("Создавать повторный запрос запрещено.");
            throw new ConflictException("Создавать повторный запрос запрещено.");
        }

        if (event.getInitiator().getId().equals(userId)) {
            log.warn("Нельзя создавать запрос на собственное событие.");
            throw new ConflictException("Нельзя создавать запрос на собственное событие.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            log.warn("Нельзя создавать запрос на неопубликованное событие.");
            throw new ConflictException("Нельзя создавать запрос на неопубликованное событие.");
        }

        ParticipationRequest newRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            newRequest.setStatus(ParticipationRequestStatus.PENDING);
        }

        newRequest = requestRepository.save(newRequest);
        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(newRequest);
    }

    @Override
    public EventRequestStatusUpdateResult updateEventRequestsStatusByEventOwner(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        log.info("Изменение статуса заявок на участие в событии с id = {} от пользователя с id = {}", eventId, userId);
        checkUser(userId);
        Event event = checkEvent(eventId);

        checkUserIsOwner(event.getInitiator().getId(), userId);

        if (!event.getRequestModeration() ||
                event.getParticipantLimit() == 0 ||
                eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
            return new EventRequestStatusUpdateResult(List.of(), List.of());
        }

        List<ParticipationRequest> confirmedList = new ArrayList<>();
        List<ParticipationRequest> rejectedList = new ArrayList<>();

        List<ParticipationRequest> requests = requestRepository.
                findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
            log.warn("Были найдены не все требуемые запросы на участие в событии");
            throw new NotFoundException("Были найдены не все требуемые запросы на участие в событии");
        }

        if (!requests.stream()
                .map(ParticipationRequest::getStatus)
                .allMatch(ParticipationRequestStatus.PENDING::equals)) {
            log.warn("Обновление статуса возможно у заявок находящихся в статусе ожидания");
            throw new ConflictException("Обновление статуса возможно у заявок находящихся в статусе ожидания");
        }

        if (eventRequestStatusUpdateRequest.getStatus().equals(ParticipationRequestStatus.REJECTED)) {
            rejectedList.addAll(updateStatus(requests, ParticipationRequestStatus.REJECTED, event));
        } else {
            Integer count = event.getConfirmedRequests();
            Integer limit = event.getParticipantLimit();
            try {
                if (limit > 0 && count.equals(limit)) {
                    rejectedList.addAll(updateStatus(
                            requestRepository.findAllByEventIdAndStatus(eventId, ParticipationRequestStatus.PENDING),
                            ParticipationRequestStatus.REJECTED, event));
                } else if (limit > 0 && count < limit) {
                    int countPlus = count + eventRequestStatusUpdateRequest.getRequestIds().size();
                    if (countPlus > limit) {
                        requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds())
                                .stream()
                                .limit(limit - count)
                                .collect(Collectors.toList());
                        confirmedList.addAll(updateStatus(requests, ParticipationRequestStatus.CONFIRMED, event));
                    } else {
                        confirmedList.addAll(updateStatus(requests, ParticipationRequestStatus.CONFIRMED, event));
                    }
                }
                checkNewLimit(count, limit);
            } catch (ConflictException ex) {
                log.warn("Достигнут лимит подтвержденных запросов на участие: {}", limit);
                throw new ConflictException(String.format(
                        "Достигнут лимит подтвержденных запросов на участие: %d", limit));
            }

        }

        return new EventRequestStatusUpdateResult(toListDto(confirmedList),
                toListDto(rejectedList));
    }

    private void checkNewLimit(Integer count, Integer limit) {
        if (limit != 0 && limit <= count) {
            log.warn("Достигнут лимит подтвержденных запросов на участие: {}", limit);
            throw new ConflictException(String.format("Достигнут лимит подтвержденных запросов на участие: %d", limit));
        }
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByRequester(Long userId) {
        checkUser(userId);

        return toListDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId) {
        checkUser(userId);
        Event event = checkEvent(eventId);

        checkUserIsOwner(event.getInitiator().getId(), userId);

        return toListDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
        log.info("Отмена запроса на участие в событии по id = {} от пользователя с id = {}.", requestId, userId);
        checkUser(userId);

        ParticipationRequest request = checkParticipationRequest(requestId);

        checkUserIsOwner(request.getRequester().getId(), userId);

        request.setStatus(ParticipationRequestStatus.CANCELED);

        return ParticipationRequestMapper.INSTANCE.toParticipationRequestDto(requestRepository.save(request));
    }

    private List<ParticipationRequestDto> toListDto(List<ParticipationRequest> requests) {
        return requests.stream()
                .map(ParticipationRequestMapper.INSTANCE::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    private List<ParticipationRequest> updateStatus(List<ParticipationRequest> requests, ParticipationRequestStatus status, Event event) {
        int confirmedRequestsCount = event.getConfirmedRequests();
        List<ParticipationRequest> updatedRequests = new ArrayList<>();
        for (ParticipationRequest request : requests) {
            if (status == ParticipationRequestStatus.CONFIRMED) {
                confirmedRequestsCount++;
            }
            request.setStatus(status);
            updatedRequests.add(request);
        }
        event.setConfirmedRequests(confirmedRequestsCount);
        eventRepository.save(event);
        return requestRepository.saveAll(updatedRequests);
    }

    private void checkUserIsOwner(Long id, Long userId) {
        if (!id.equals(userId)) {
            log.warn("Пользователь не является инициатором.");
            throw new ConflictException("Пользователь не является инициатором.");
        }
    }

    private User checkUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> {
                    log.warn("Не найден пользователь с id={}: ", userId);
                    return new NotFoundException(String.format(
                            "Не найден пользователь с id=%d", userId));
                }
        );
    }

    private Event checkEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> {
                    log.warn("Не найдено событие с id={}: ", eventId);
                    return new NotFoundException(String.format(
                            "Не найдено событие с id=%d", eventId));
                }
        );
    }

    private ParticipationRequest checkParticipationRequest(Long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
                    log.warn("Не найдена заявка с id={}: ", requestId);
                    return new NotFoundException(String.format(
                            "Не найдена заявка с id=%d: ", requestId));
                }
        );
    }

}
