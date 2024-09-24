package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminEventService {
    List<EventFullDto> getByFilters(List<Integer> users, List<EventState> states, List<Integer> categories,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    EventFullDto update(int eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
