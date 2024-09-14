package ru.practicum.ewm.hit;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.hit.service.HitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HitController {

    private final HitService hitService;
    private static final String patternString = "yyyy-MM-dd HH:mm:ss";
    
    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@Valid @RequestBody EndpointHitDto hitDto) {
        log.info("POST request to /hit endpoint.");
        hitService.add(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> get(@RequestParam("start") @DateTimeFormat(pattern = patternString) LocalDateTime start,
                                  @RequestParam("end")  @DateTimeFormat(pattern = patternString) LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false) Boolean unique) {
        log.info("GET request to /stats endpoint.");
        return hitService.get(start, end, uris, unique);
    }
}
