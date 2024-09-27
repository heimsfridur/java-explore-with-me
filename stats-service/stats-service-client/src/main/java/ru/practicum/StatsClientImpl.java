package ru.practicum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClientImpl implements StatsClient {

    private final WebClient webClient;
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    public StatsClientImpl(WebClient.Builder webClientBuilder,
                           @Value("${stats-server.url}") String statsServerUrl) {
        webClient = webClientBuilder.baseUrl(statsServerUrl).build();
    }

    @Override
    public void addHit(EndpointHitDto hitDto) {
        webClient.post()
                .uri("/hit")
                .bodyValue(hitDto)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/stats")
                        .queryParam("start", start.format(DateTimeFormatter.ofPattern(pattern)))
                        .queryParam("end", end.format(DateTimeFormatter.ofPattern(pattern)))
                        .queryParam("uris", uris != null ? String.join(",", uris) : null)
                        .queryParam("unique", unique != null ? unique.toString() : null)
                        .build())
                .retrieve()
                .bodyToFlux(ViewStatsDto.class)
                .collectList()
                .block();
    }
}
