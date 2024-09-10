package hit;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

public class HitMapper {
    public static Hit toHit(EndpointHitDto endpointHitDto) {
        return Hit.builder()
                .id(endpointHitDto.getId())
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
