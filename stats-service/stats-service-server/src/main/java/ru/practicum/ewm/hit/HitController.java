package ru.practicum.ewm.hit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.hit.service.HitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class HitController {

    private final HitService hitService;
    private static final String patternString = "yyyy-MM-dd HH:mm:ss";
//    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternString);

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto hitDto) {
        log.info("POST request to /hit endpoint.");
        hitService.add(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false) Boolean unique) {
        log.info("GET request to /stats endpoint.");
//        LocalDateTime start = LocalDateTime.parse(startString, formatter);
//        LocalDateTime end = LocalDateTime.parse(endString, formatter);
        return hitService.get(start, end, uris, unique);
    }
}
