package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.PrivateEventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAll(@PathVariable int userId,
                                      @RequestParam(defaultValue = "0") int from,
                                      @RequestParam(defaultValue = "10") int size) {
        return privateEventService.getAllByUserId(userId, from, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto create(@PathVariable int userId,
                               @RequestBody @Valid NewEventDto newEventDto) {
        return privateEventService.create(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getByIdByInitiator(@PathVariable int userId,
                                           @PathVariable int eventId) {
        return privateEventService.getByIdByInitiator(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEventByUser(@PathVariable int userId,
                                          @PathVariable int eventId,
                                          @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        return privateEventService.updateEventByUser(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsToEventByUser(@PathVariable int userId,
                                                                  @PathVariable int eventId) {
        return privateEventService.getRequestsToEventByUser(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable int userId,
                                                              @PathVariable int eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        return privateEventService.updateRequestStatus(userId, eventId, eventRequestStatusUpdateRequest);
    }
}
