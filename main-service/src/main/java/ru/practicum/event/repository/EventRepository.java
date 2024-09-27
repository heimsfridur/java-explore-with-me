package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import org.springframework.data.domain.Pageable;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    boolean existsByCategoryId(int categoryId);

    List<Event> findAllByInitiatorId(int id, Pageable pageable);

    Event findByIdAndInitiatorId(int eventId, int userId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:text IS NULL OR LOWER(e.annotation) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', COALESCE(:text, ''), '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (:onlyAvailable = FALSE OR e.confirmedRequests < e.participantLimit) " +
            "AND e.state = 'PUBLISHED'")
    List<Event> findAllWithFilters(String text, List<Integer> categories, Boolean paid,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                   Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND (e.eventDate BETWEEN :rangeStart AND :rangeEnd) " +
            "AND (:onlyAvailable = FALSE OR e.confirmedRequests < e.participantLimit) " +
            "AND e.state = 'PUBLISHED'")
    List<Event> findAllWithFiltersNullText(List<Integer> categories, Boolean paid,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                   Boolean onlyAvailable, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (e.eventDate >= :rangeStart AND e.eventDate <= :rangeEnd)")
    List<Event> findAllWithFiltersForAdmin(List<Integer> users, List<EventState> states, List<Integer> categories,
                                           LocalDateTime rangeStart, LocalDateTime  rangeEnd, Pageable pageable);
}
