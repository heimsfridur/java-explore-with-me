package ru.practicum.event.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getAllByUserId(int userId, int from, int size);

    EventFullDto create(int userId, NewEventDto newEventDto);

    EventFullDto getByIdByInitiator(int userId, int eventId);

    EventFullDto updateEventByUser(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsToEventByUser(int userId, int eventId);
}
