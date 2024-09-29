package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PublicEventServiceImpl implements PublicEventService {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getWithFilters(String text, List<Integer> categories,
                                              Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable,
                                              String sort, int from, int size) {

        EventUtils.validateCategories(categories, categoryRepository);

        rangeStart = EventUtils.validateRangeStart(rangeStart);
        rangeEnd = EventUtils.validateRangeEnd(rangeEnd);

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
                    Long views = EventUtils.getViewsNumber(event, statsClient);
                    event.setViews(views);
                    return EventMapper.toEventShortDto(event);
                })
                .toList();
    }

    @Override
    public EventFullDto getEvent(int id) {

        Event event = EventUtils.getEventById(id, eventRepository);


        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException(String.format("There are no published events with id %d", id));
        }

        event.setViews(EventUtils.getViewsNumber(event, statsClient));

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }
}
