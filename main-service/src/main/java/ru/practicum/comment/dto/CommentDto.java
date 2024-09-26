package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Integer id;
    private Integer eventId;
    private Integer authorId;

    @NotBlank
    private String text;
    private LocalDateTime created;
    private LocalDateTime lastUpdateTime;
}