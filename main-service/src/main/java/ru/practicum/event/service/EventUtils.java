package ru.practicum.event.service;

import ru.practicum.StatsClient;
import ru.practicum.ViewStatsDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.UpdateEventRequest;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.BadRequestException;
import ru.practicum.exceptions.NotFoundException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.mapper.LocationMapper;

import java.time.LocalDateTime;
import java.util.List;

public class EventUtils {

    public static Event getEventById(int id, EventRepository eventRepository) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id %d is not found.", id)));
    }

    public static LocalDateTime validateRangeStart(LocalDateTime rangeStart) {
        if (rangeStart == null) {
            return LocalDateTime.of(1900, 1, 1, 0, 0);
        }
        return rangeStart;
    }

    public static LocalDateTime validateRangeEnd(LocalDateTime rangeEnd) {
        if (rangeEnd == null) {
            return LocalDateTime.of(3000, 1, 1, 0, 0);
        }
        return rangeEnd;
    }

    public static void validateCategories(List<Integer> categories, CategoryRepository categoryRepository) {
        if (categories != null) {
            categories.stream().forEach(categoryId -> {
                if (!categoryRepository.existsById(categoryId)) {
                    throw new BadRequestException(String.format("Category with id %d is not found.", categoryId));
                }
            });
        }
    }

    public static Long getViewsNumber(Event event, StatsClient statsClient) {
        String eventUri = "/events/" + event.getId();
        LocalDateTime start = event.getPublishedOn() != null ? event.getPublishedOn() : event.getCreatedOn();
        LocalDateTime end = LocalDateTime.now();

        List<ViewStatsDto> viewStats = statsClient.get(start, end, List.of(eventUri), true);
        if (viewStats.isEmpty()) {
            return 0L;
        }
        return viewStats.get(0).getHits();
    }

    public static void updateEventFields(Event event, UpdateEventRequest updateEventAdminRequest,
                                         CategoryRepository categoryRepository, int dateHoursValidation) {

        LocalDateTime newEventDate = updateEventAdminRequest.getEventDate();
        if (newEventDate != null) {
            if (updateEventAdminRequest.getEventDate().isBefore(LocalDateTime.now().plusHours(dateHoursValidation))) {
                throw new BadRequestException(String.format("Updated event time cannot be earlier than hour from the current moment", dateHoursValidation));
            }
            event.setEventDate(newEventDate);
        }

        String newAnnotation = updateEventAdminRequest.getAnnotation();
        if (newAnnotation != null) {
            event.setAnnotation(newAnnotation);
        }

        Integer newCategoryDtoId = updateEventAdminRequest.getCategory();
        if (newCategoryDtoId != null) {
            Category category = categoryRepository.findById(newCategoryDtoId)
                    .orElseThrow(() -> new BadRequestException("Category not found"));
            event.setCategory(category);
        }

        String newDescription = updateEventAdminRequest.getDescription();
        if (newDescription != null) {
            event.setDescription(newDescription);
        }

        LocationDto newLocation = updateEventAdminRequest.getLocation();
        if (newLocation != null) {
            event.setLocation(LocationMapper.toLocation(newLocation));
        }

        Boolean newPaid = updateEventAdminRequest.getPaid();
        if (newPaid != null) {
            event.setPaid(newPaid);
        }

        Integer newParticipantLimit = updateEventAdminRequest.getParticipantLimit();
        if (newParticipantLimit != null) {
            event.setParticipantLimit(newParticipantLimit);
        }

        Boolean newRequestModeration = updateEventAdminRequest.getRequestModeration();
        if (newRequestModeration != null) {
            event.setRequestModeration(newRequestModeration);
        }

        String newTitle = updateEventAdminRequest.getTitle();
        if (newTitle != null) {
            event.setTitle(newTitle);
        }
    }
}
