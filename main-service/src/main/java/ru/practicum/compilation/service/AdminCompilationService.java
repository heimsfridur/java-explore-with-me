package ru.practicum.compilation.service;

import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto create(NewCompilationDto newCompilationDto);

    void delete(int compId);

    CompilationDto update(int compId, UpdateCompilationRequest updateCompilationRequest);
}
