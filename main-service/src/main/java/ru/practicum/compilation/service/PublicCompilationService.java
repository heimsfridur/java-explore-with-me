package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;

import java.util.List;

public interface PublicCompilationService {
    List<CompilationDto> getWithFilters(Boolean pinned, int from, int size);

    CompilationDto getById(int compId);
}
