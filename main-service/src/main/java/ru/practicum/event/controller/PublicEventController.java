package ru.practicum.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.PublicEventService;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/events")
public class PublicEventController {
    private final PublicEventService publicEventService;
    private final String pattern = "yyyy-MM-dd HH:mm:ss";
    private final StatsClient statsClient;

    @GetMapping
    public List<EventShortDto> getWithFilters(@RequestParam(required = false) String text,
                                              @RequestParam(required = false) List<Integer> categories,
                                              @RequestParam(required = false) Boolean paid,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeStart,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = pattern) LocalDateTime rangeEnd,
                                              @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                              @RequestParam(required = false) String sort,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") @Positive int size,
                                              HttpServletRequest request
                                   ) {
        List<EventShortDto> filteredEvents = publicEventService.getWithFilters(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size);
        saveStats(request);
        return filteredEvents;

    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable int id, HttpServletRequest request) {
        EventFullDto event = publicEventService.getEvent(id);
        saveStats(request);
        return event;
    }

    private void saveStats(HttpServletRequest request) {
        statsClient.addHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }
}
