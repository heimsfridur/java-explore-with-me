package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getWithFilters(String text, Set<Integer> categories,
                                              Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable,
                                              String sort, int from, int size) {
        if (categories != null) {
            categories.stream().forEach(categoryId -> validateCategoryById(categoryId));
        }

        if (rangeStart == null) {
            rangeStart = LocalDateTime.of(1900, 1, 1, 0, 0);
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.of(3000, 1, 1, 0, 0);
        }

        Sort sortCriteria = Sort.by(Sort.Direction.ASC, "eventDate");
        if ("VIEWS".equalsIgnoreCase(sort)) {
            sortCriteria = Sort.by(Sort.Direction.DESC, "views");
        }

        Pageable pageable = PageRequest.of(from / size, size, sortCriteria);

        List<Event> events;

        if (text == null || text.isEmpty()) {
            events = eventRepository.findAllWithFiltersNullText(categories, paid,
                    rangeStart, rangeEnd, onlyAvailable, pageable);
        } else {
            events = eventRepository.findAllWithFilters(text, categories, paid,
                    rangeStart, rangeEnd, onlyAvailable, pageable);
        }


        return events.stream()
                .map(event -> {
                    Long views = getViewsNumber(event);
                    event.setViews(views);
                    return EventMapper.toEventShortDto(event);
                })
                .toList();
    }

    @Override
    public EventFullDto getEvent(int id) {

        validateEventById(id);
        Event event = eventRepository.findById(id).get();

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("There are no published events with id %d", id));
        }

        event.setViews(getViewsNumber(event));

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    private void validateCategoryById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new BadRequestException(String.format("Category with id %d is not found.", id));
        }
    }

    private void validateEventById(int id) {
        if (!eventRepository.existsById(id)) {
            throw new NotFoundException(String.format("Event with id %d is not found.", id));
        }
    }

    private Long getViewsNumber(Event event) {
        String eventUri = "/events/" + event.getId();
        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn() : event.getCreatedOn();
        LocalDateTime end = LocalDateTime.now();

        List<ViewStatsDto> viewStats = statsClient.get(start, end, List.of(eventUri), true);
        if (viewStats.isEmpty()) {
            return 0L;
        }
        return viewStats.get(0).getHits();
    }
}
