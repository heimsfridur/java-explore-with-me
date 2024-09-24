package ru.practicum.request.service;

import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsForUser(int userId);

    ParticipationRequestDto create(int userId, int eventId);

    ParticipationRequestDto cancel(int userId, int requestId);
}
