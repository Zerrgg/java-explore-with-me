package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    List<Event> findAllByIdIn(List<Long> eventsIds);

    List<Event> findAllByCategoryId(Long catId);

    @Query("select e from Event as e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and e.eventDate > :rangeStart")
    List<Event> getEventsByAdmin(@Param("users") List<Long> users,
                                 @Param("states") List<EventState> states,
                                 @Param("categories") List<Long> categories,
                                 @Param("rangeStart") LocalDateTime rangeStart,
                                 PageRequest pageable);

    @Query("select e from Event as e " +
            "where (upper(e.annotation) like upper(concat('%', :text, '%')) " +
            "or upper(e.description) like upper(concat('%', :text, '%')) or :text is null) " +
            "and e.state = :state " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and e.eventDate >= :rangeStart")
    List<Event> getEventsSort(@Param("text") String text,
                              @Param("state") EventState state,
                              @Param("categories") List<Long> categories,
                              @Param("paid") Boolean paid,
                              @Param("rangeStart") LocalDateTime rangeStart,
                              Pageable pageable);
}
