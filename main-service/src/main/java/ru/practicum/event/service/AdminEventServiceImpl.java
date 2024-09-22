package ru.practicum.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.AdminEventStateAction;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.ConflictException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;

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
        categories.stream().forEach(categoryId -> validateCategoryById(categoryId));

        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.MAX;
        }

        Pageable pageable = PageRequest.of(from / size, size);

        List<Event> events = eventRepository.findAllWithFiltersForAdmin(users, states, categories,
                rangeStart, rangeEnd, pageable);

        return events.stream()
                .map(event -> {
                    Long views = getViewsNumber(event);
                    event.setViews(views);
                    return EventMapper.toEventFullDto(event);
                })
                .toList();
    }

    @Override
    public EventFullDto update(int eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        validateEventById(eventId);
        Event oldEvent = eventRepository.findById(eventId).get();

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


        LocalDateTime newEventDate = updateEventAdminRequest.getEventDate();
        if (newEventDate != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
                throw new ConflictException("Updated event time cannot be earlier than hour from the current moment");
            }
            oldEvent.setEventDate(newEventDate);
        }

        String newAnnotation = updateEventAdminRequest.getAnnotation();
        if (newAnnotation != null) {
            oldEvent.setAnnotation(newAnnotation);
        }

        Integer newCategoryDtoId = updateEventAdminRequest.getCategory();
        if (newCategoryDtoId != null) {
            Category category = categoryRepository.findById(newCategoryDtoId).get();
            oldEvent.setCategory(category);
        }

        String newDescription = updateEventAdminRequest.getDescription();
        if (newDescription != null) {
            oldEvent.setDescription(newDescription);
        }

        LocationDto newLocation = updateEventAdminRequest.getLocation();
        if (newLocation != null) {
            oldEvent.setLocation(LocationMapper.toLocation(newLocation));
        }

        Boolean newPaid = updateEventAdminRequest.getPaid();
        if (newPaid != null) {
            oldEvent.setPaid(newPaid);
        }

        Integer newParticipantLimit = updateEventAdminRequest.getParticipantLimit();
        if (newParticipantLimit != null) {
            oldEvent.setParticipantLimit(newParticipantLimit);
        }

        Boolean newRequestModeration = updateEventAdminRequest.getRequestModeration();
        if (newRequestModeration != null) {
            oldEvent.setRequestModeration(newRequestModeration);
        }

        String newTitle = updateEventAdminRequest.getTitle();
        if (newTitle != null) {
            oldEvent.setTitle(newTitle);
        }

        return EventMapper.toEventFullDto(eventRepository.save(oldEvent));
    }


    private void validateCategoryById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException(String.format("Category with id %d is not found.", id));
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

