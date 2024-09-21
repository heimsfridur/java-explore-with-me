package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface PrivateEventService {
    List<EventShortDto> getAllByUserId(int userId, int from, int size);

    EventFullDto create(int userId, NewEventDto newEventDto);

    EventFullDto getByIdByInitiator(int userId, int eventId);

    EventFullDto updateEventByUser(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequestsToEventByUser(int userId, int eventId);

    EventRequestStatusUpdateResult updateRequestStatus(int userId, int eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
