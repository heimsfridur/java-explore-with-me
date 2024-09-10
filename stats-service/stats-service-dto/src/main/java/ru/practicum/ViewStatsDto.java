package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class ViewStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
