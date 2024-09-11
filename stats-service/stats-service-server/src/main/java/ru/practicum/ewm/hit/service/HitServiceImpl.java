package ru.practicum.ewm.hit.service;

import ru.practicum.ewm.hit.Hit;
import ru.practicum.ewm.hit.HitMapper;
import ru.practicum.ewm.hit.HitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    public void add(EndpointHitDto hitDto) {
        hitRepository.save(HitMapper.toHit(hitDto));
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        List<Hit> hitList;

        if (!unique) {
            if (uris == null || uris.isEmpty()) {
                // not unique ip, not specific uris
                hitList = hitRepository.findAllByTimestampBetween(start, end);
            } else {
                // not unique ip, specific uris
                hitList = hitRepository.findAllByUriInAndTimestampBetween(uris, start, end);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                // unique ip, not specific uris
                hitList = hitRepository.findDistinctByTimestampBetween(start, end);
            } else {
                // unique ip, specific uris
                hitList = hitRepository.findDistinctByUriInAndTimestampBetween(uris, start, end);
            }
        }

        Map<String, Map<String, Long>> hitsGroupedByAppAndUri = hitList.stream()
                .collect(Collectors.groupingBy(
                        Hit::getApp,
                        Collectors.groupingBy(
                                Hit::getUri,
                                Collectors.counting()
                        )
                ));

        List<ViewStatsDto> viewStatsList = new ArrayList<>();

        hitsGroupedByAppAndUri.forEach((app, uriCountMap) -> uriCountMap.forEach((uri, count) -> {
            ViewStatsDto viewStatsDto = ViewStatsDto.builder()
                    .app(app)
                    .uri(uri)
                    .hits(count)
                    .build();
            viewStatsList.add(viewStatsDto);
        }));

        return viewStatsList;

    }


}
