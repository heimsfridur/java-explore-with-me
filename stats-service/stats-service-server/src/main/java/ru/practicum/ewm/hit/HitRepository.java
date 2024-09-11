package ru.practicum.ewm.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {
    List<Hit> findAllByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Hit> findAllByUriInAndTimestampBetween(List<String> uris, LocalDateTime start, LocalDateTime end);

    List<Hit> findDistinctByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<Hit> findDistinctByUriInAndTimestampBetween(List<String> uris, LocalDateTime start, LocalDateTime end);
}
