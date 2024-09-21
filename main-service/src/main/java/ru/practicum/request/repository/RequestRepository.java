package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findAllByRequesterId(int userId);

    Boolean existsByRequesterIdAndEventId(int userId, int eventId);

    List<Request> findAllByRequesterIdAndEventId(int userId, int eventId);
}
