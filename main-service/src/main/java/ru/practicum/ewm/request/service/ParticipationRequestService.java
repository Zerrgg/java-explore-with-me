package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    ParticipationRequestDto addEventRequest(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateEventRequestsStatusByEventOwner(
            Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);

    List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId);

    List<ParticipationRequestDto> getEventRequestsByRequester(Long userId);

    ParticipationRequestDto cancelEventRequest(Long userId, Long requestId);
}