package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.location.dto.LocationDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NewEventDto {
    @NotBlank(message = "Annotation is required")
    @Size(min = 20, max = 2000, message = "Min length for event annotation should be 20. Max - 2000.")
    private String annotation;

    @NotNull(message = "Category is required")
    private Integer category;

    @NotBlank(message = "Description is required")
    @Size(min = 20, max = 7000, message = "Min length for event description should be 20. Max - 7000.")
    private String description;

    @NotNull(message = "Event date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Location is required")
    private LocationDto location;

    @JsonProperty(defaultValue = "false")
    @Builder.Default
    private Boolean paid = false;

    @JsonProperty(defaultValue = "0")
    @Builder.Default
    @PositiveOrZero(message = "Participants limit should not be negative.")
    private Integer participantLimit = 0;

    @JsonProperty(defaultValue = "true")
    @Builder.Default
    private Boolean requestModeration = true;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 120, message = "Min length for event title should be 3. Max - 120")
    private String title;

}
