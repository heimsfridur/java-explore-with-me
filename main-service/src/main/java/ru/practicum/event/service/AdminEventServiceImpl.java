package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.AdminEventStateAction;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> getByFilters(List<Integer> users, List<EventState> states, List<Integer> categories,
                              LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {

        EventUtils.validateCategories(categories, categoryRepository);

        rangeStart = EventUtils.validateRangeStart(rangeStart);
        rangeEnd = EventUtils.validateRangeEnd(rangeEnd);

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllWithFiltersForAdmin(users, states, categories,
                rangeStart, rangeEnd, pageable);

        return events.stream()
                .map(event -> {
                    Long views = EventUtils.getViewsNumber(event, statsClient);
                    event.setViews(views);
                    return EventMapper.toEventFullDto(event);
                })
                .toList();
    }

    @Override
    public EventFullDto update(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {

        Event oldEvent = EventUtils.getEventById(eventId, eventRepository);

        AdminEventStateAction newStateAction = updateEventAdminRequest.getStateAction();

        if (newStateAction != null) {
            if (newStateAction.equals(AdminEventStateAction.PUBLISH_EVENT)
                    && !oldEvent.getState().equals(EventState.PENDING)) {
                throw new ConflictException("It's possible to publish only PENDING event.");
            }
            if (newStateAction.equals(AdminEventStateAction.REJECT_EVENT)
                    && oldEvent.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("It's possible to reject published event.");
            }

            if (newStateAction.equals(AdminEventStateAction.PUBLISH_EVENT)) {
                oldEvent.setState(EventState.PUBLISHED);
                oldEvent.setPublishedOn(LocalDateTime.now());
            } else if (newStateAction.equals(AdminEventStateAction.REJECT_EVENT)) {
                oldEvent.setState(EventState.CANCELED);
            }
        }

        EventUtils.updateEventFields(oldEvent, updateEventAdminRequest, categoryRepository, 1);

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));
    }
}

