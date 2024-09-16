package ru.practicum.ewm.hit.service;

import ru.practicum.ewm.hit.HitMapper;
import ru.practicum.ewm.hit.HitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {
    private final HitRepository hitRepository;

    public void add(EndpointHitDto hitDto) {
        hitRepository.save(HitMapper.toHit(hitDto));
    }

    public List<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {

        if (unique == null || !unique) {
            if (uris == null || uris.isEmpty()) {
                // not unique ip, not specific uris
                return hitRepository.findAllByTimestampBetween(start, end);
            } else {
                // not unique ip, specific uris
                return hitRepository.findAllByUriInAndTimestampBetween(uris, start, end);
            }
        } else {
            if (uris == null || uris.isEmpty()) {
                // unique ip, not specific uris
                return hitRepository.findDistinctByTimestampBetween(start, end);
            } else {
                // unique ip, specific uris
                return hitRepository.findDistinctByUriInAndTimestampBetween(uris, start, end);
            }
        }
    }
}
