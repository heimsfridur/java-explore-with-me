package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.UserEventStateAction;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.RequestStatus;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<EventShortDto> getAllByUserId(int userId, int from, int size) {
        validateUserById(userId);

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);

        return events.stream().map(EventMapper::toEventShortDto).toList();
    }

    @Override
    public EventFullDto create(int userId, NewEventDto newEventDto) {
        LocalDateTime currentTime = LocalDateTime.now();

        validateUserById(userId);
        validateNewEventDate(newEventDto.getEventDate());

        Event event = EventMapper.toEventFromNewEventDto(newEventDto);
        event.setCreatedOn(currentTime);
        event.setConfirmedRequests(0);
        event.setInitiator(userRepository.findById(userId).get());
        event.setState(EventState.PENDING);

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getByIdByInitiator(int userId, int eventId) {
        validateUserById(userId);

        Event event = EventUtils.getEventById(eventId, eventRepository);

        validateInitiator(event, userId);

        return EventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto updateEventByUser(int userId, int eventId, UpdateEventUserRequest updateEventUserRequest) {
        LocalDateTime currentTime = LocalDateTime.now();

        validateUserById(userId);

        Event oldEvent = EventUtils.getEventById(eventId, eventRepository);


        validateInitiator(oldEvent, userId);

        if (oldEvent.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only PENDING or CANCELED events could be updated.");
        }


        UserEventStateAction newStateAction = updateEventUserRequest.getStateAction();
        if (newStateAction != null) {
            if (newStateAction.equals(UserEventStateAction.SEND_TO_REVIEW)) {
                oldEvent.setState(EventState.PENDING);
            } else if (newStateAction.equals(UserEventStateAction.CANCEL_REVIEW)) {
                oldEvent.setState(EventState.CANCELED);
            }
        }

        EventUtils.updateEventFields(oldEvent, updateEventUserRequest, categoryRepository, 2);

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));


    }

    @Override
    public List<ParticipationRequestDto> getRequestsToEventByUser(int userId, int eventId) {
        validateUserById(userId);

        Event event = EventUtils.getEventById(eventId, eventRepository);

        validateInitiator(event, userId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);
        return requests.stream().map(RequestMapper::toParticipationRequestDto).toList();
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(int userId, int eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        validateUserById(userId);

        Event event = EventUtils.getEventById(eventId, eventRepository);

        validateInitiator(event, userId);

        int participantsLimit = event.getParticipantLimit();

        if (participantsLimit != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ConflictException("No free spaces for the event.");
        }

        RequestStatus status = eventRequestStatusUpdateRequest.getStatus();

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();


        List<Request> requests = requestRepository.findAllById(eventRequestStatusUpdateRequest.getRequestIds());
        requests.forEach(request -> {
            if (request.getStatus().equals(RequestStatus.PENDING)) {
                if (participantsLimit == 0) {
                    request.setStatus(RequestStatus.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                } else if (participantsLimit > event.getConfirmedRequests()) {
                    if (!event.getRequestModeration() || status.equals(RequestStatus.CONFIRMED)) {
                        request.setStatus(RequestStatus.CONFIRMED);
                        event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                        confirmed.add(RequestMapper.toParticipationRequestDto(request));
                    } else {
                        request.setStatus(RequestStatus.REJECTED);
                        rejected.add(RequestMapper.toParticipationRequestDto(request));
                    }
                    requestRepository.save(request);
                }
            } else {
                throw new ConflictException("Status could be changed only in PENDING requests.");
            }
        });
        eventRepository.save(event);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed)
                .rejectedRequests(rejected)
                .build();
    }


    private void validateUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("User with id %d is not found.", id));
        }
    }

    private void validateInitiator(Event event, int userId) {
        if (!event.getInitiator().getId().equals(userId)) {
            throw new ConflictException(String.format("User with id %d is not initiator of event with id %d", userId, event.getId()));
        }
    }

    private void validateNewEventDate(LocalDateTime time) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime minimumAllowedTime = currentTime.plusHours(2);

        if (time.isBefore(minimumAllowedTime)) {
            throw new BadRequestException("Event time cannot be earlier than two hours from the current moment");
        }
    }


}
