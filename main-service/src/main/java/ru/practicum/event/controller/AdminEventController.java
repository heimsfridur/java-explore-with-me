package ru.practicum.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.AdminEventService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/admin/events")
public class AdminEventController {
    private final AdminEventService adminEventService;
    private final String pattern = "yyyy-MM-dd HH:mm:ss";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getByFilters(@RequestParam(required = false) List<Integer> users,
                                     @RequestParam(required = false) List<EventState> states,
                                     @RequestParam(required = false) List<Integer> categories,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeStart,
                                     @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeEnd,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") @Positive int size) {
        return adminEventService.getByFilters(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto update(@PathVariable int eventId,
                               @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        return adminEventService.update(eventId, updateEventAdminRequest);
    }

}
