package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.dto.EventShortDto;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCompilationRequest {
    private Set<Integer> events;

    private Integer id;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Min length for compilation title should be 1. Max - 50")
    private String title;
}
