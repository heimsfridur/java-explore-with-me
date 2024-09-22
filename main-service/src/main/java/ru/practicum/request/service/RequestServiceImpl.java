package ru.practicum.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getRequestsForUser(int userId) {
        validateUserById(userId);
        List<Request> requests = requestRepository.findAllByRequesterId(userId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public ParticipationRequestDto create(int userId, int eventId) {
        validateUserById(userId);
        validateEventById(eventId);

        User user = userRepository.findById(userId).get();
        Event event = eventRepository.findById(eventId).get();

        if (event.getInitiator().equals(userId)) {
            throw new ConflictException("Impossible to add request for the event user made.");
        }

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Impossible to participate in unpublished event.");
        }

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException(String.format("User with id %d is already participating in event with id %d.", userId, eventId));
        }

        if (event.getConfirmedRequests() > event.getParticipantLimit()) {
            throw new ConflictException("No free spaces in event.");
        }

        RequestStatus status;

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            status = RequestStatus.PENDING;
        }

        Request request = Request.builder()
                .created(LocalDateTime.now().withNano(0))
                .event(event)
                .requester(user)
                .status(status)
                .build();

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancel(int userId, int requestId) {
        validateUserById(userId);
        validateRequestById(requestId);
        Request request = requestRepository.findById(requestId).get();
        request.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private void validateUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }

    private void validateEventById(int id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Event with id %d is not found.", id));
        }
    }

    private void validateRequestById(int id) {
        if (!requestRepository.existsById(id)) {
            throw new NotFoundException(String.format("Request with id %d is not found.", id));
        }
    }
}
