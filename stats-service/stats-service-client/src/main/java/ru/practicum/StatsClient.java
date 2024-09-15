package ru.practicum;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsClient {
    void addHit(EndpointHitDto hitDto);

    List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
