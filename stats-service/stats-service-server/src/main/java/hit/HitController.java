package hit;

import hit.service.HitService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class HitController {

    private  final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void addHit(@RequestBody EndpointHitDto hitDto) {
        hitService.add(hitDto);
    }

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                  @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(required = false) Boolean unique) {
        return hitService.get(start, end, uris, unique);
    }
}
