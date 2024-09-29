package ru.practicum.compilation.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
        }

        Compilation compilation = Compilation.builder()
                .pinned(newCompilationDto.getPinned())
                .title(newCompilationDto.getTitle())
                .events(new HashSet<>(events))
                .build();

        return CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public void delete(int compId) {
        validateCompilationById(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto update(int compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation oldCompilation = getCompilationById(compId);

        Boolean newPinned = updateCompilationRequest.getPinned();
        if (newPinned != null) {
            oldCompilation.setPinned(newPinned);
        }

        String newTitle = updateCompilationRequest.getTitle();
        if (newTitle != null) {
            oldCompilation.setTitle(newTitle);
        }

        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            oldCompilation.setEvents(events);
        }

        return CompilationMapper.toCompilationDto(compilationRepository.save(oldCompilation));
    }

    private void validateCompilationById(int id) {
        if (!compilationRepository.existsById(id)) {
            throw new NotFoundException(String.format("Compilation with id %d is not found.", id));
        }
    }

    private Compilation getCompilationById(int id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id %d is not found.", id)));
    }

}
