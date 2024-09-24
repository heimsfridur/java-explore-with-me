package ru.practicum.ewm.hit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HitRepository extends JpaRepository<Hit, Integer> {

    @Query("select new ru.practicum.ViewStatsDto(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit as hit " +
            "where hit.timestamp between :start and :end " +
            "group by hit.app, hit.uri " +
            "order by count(hit.ip) desc ")
    List<ViewStatsDto> findAllByTimestampBetween(@Param("start") LocalDateTime start,
                                                 @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(hit.app, hit.uri, COUNT(hit.ip)) " +
            "FROM Hit as hit " +
            "WHERE hit.timestamp between :start AND :end AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER By COUNT(hit.ip) DESC")
    List<ViewStatsDto> findAllByUriInAndTimestampBetween(@Param("uris") List<String> uris,
                                                         @Param("start") LocalDateTime start,
                                                         @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit as hit  " +
            "WHERE hit.timestamp between :start AND :end " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER By COUNT(DISTINCT hit.ip) DESC")
    List<ViewStatsDto> findDistinctByTimestampBetween(@Param("start") LocalDateTime start,
                                                      @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ViewStatsDto(hit.app, hit.uri, COUNT(DISTINCT hit.ip)) " +
            "FROM Hit as hit " +
            "WHERE hit.timestamp between :start AND :end AND hit.uri IN :uris " +
            "GROUP BY hit.app, hit.uri " +
            "ORDER By COUNT(DISTINCT hit.ip) DESC")
    List<ViewStatsDto> findDistinctByUriInAndTimestampBetween(@Param("uris") List<String> uris,
                                                       @Param("start") LocalDateTime start,
                                                       @Param("end") LocalDateTime end);

}
