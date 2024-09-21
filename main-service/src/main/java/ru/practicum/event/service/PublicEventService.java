package ru.practicum.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface PublicEventService {
    List<EventShortDto> getWithFilters(String text, Set<Integer> categories,
                                       Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable,
                                       String sort, int from, int size);

    EventFullDto getEvent(int id);
}
