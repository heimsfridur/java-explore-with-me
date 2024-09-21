package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    boolean existsByCategoryId(int categoryId);

    List<Event> findAllByInitiatorId(int id, Pageable pageable);

    Event findByIdAndInitiatorId(int eventId, int userId);
}
