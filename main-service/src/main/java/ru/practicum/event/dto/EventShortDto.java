package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventShortDto {
    @NotBlank(message = "Annotation is required")
    private String annotation;

    @NotNull(message = "Category is required")
    private CategoryDto category;

    private Integer confirmedRequests;

    @NotNull(message = "Event date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private Integer id;

    @NotNull(message = "Initiator is required")
    private UserShortDto initiator;

    @NotNull(message = "Paid field is required")
    private Boolean paid;

    @NotBlank(message = "Title is required")
    private String title;

    private Integer views;
}
