package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.request.enums.ParticipationRequestStatus;
import ru.practicum.ewm.request.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    Optional<ParticipationRequest> findByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> requestIds);

}
