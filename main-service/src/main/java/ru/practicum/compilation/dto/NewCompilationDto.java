package ru.practicum.compilation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewCompilationDto {
    private Set<Integer> events;

    @JsonProperty(defaultValue = "false")
    @Builder.Default
    private Boolean pinned = false;

    @NotBlank(message =  "Compilation title should not be blank")
    @Size(min = 1, max = 50, message = "Min length for compilation title should be 1. Max - 50")
    private String title;
}
