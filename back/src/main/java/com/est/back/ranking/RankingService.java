package com.est.back.ranking;

import com.est.back.ranking.dto.RankingDto;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @PersistenceContext
    private EntityManager em;

    // 이번 주 월요일 00:00
    private LocalDateTime getWeekStartTime() {
        return LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(); //.atTime(LocalTime.of(5, 15)); //Mon. 05:15
    }

    //랭킹 조회
    @Cacheable(value = "Ranking", key = "'top10'")
    public List<RankingDto> getRanking() {
        String query = """
            SELECT f.usn, u.nickName, u.gender, u.profile_img, f.score, f.created_at
            FROM blind_date_feedback f
            JOIN USER u ON f.usn = u.usn
            WHERE f.created_at >= :weekStart
              AND f.score = (
                SELECT MAX(f2.score)
                FROM blind_date_feedback f2
                WHERE f2.usn = f.usn AND f2.created_at >= :weekStart
              )
              AND f.created_at = (
                SELECT MIN(f3.created_at)
                FROM blind_date_feedback f3
                WHERE f3.usn = f.usn AND f3.score = f.score AND f3.created_at >= :weekStart
              )
            GROUP BY f.usn, u.nickName, u.gender, u.profile_img, f.score, f.created_at
            ORDER BY f.score DESC, f.created_at ASC
            LIMIT 10
        """;

        List<Object[]> result = em.createNativeQuery(query)
                .setParameter("weekStart", getWeekStartTime())
                .getResultList();

        return result.stream().map(row -> RankingDto.builder()
                .usn(((Number) row[0]).longValue())
                .nickname((String) row[1])
                .gender((String) row[2])
                .profileImg((String) row[3])
                .score(((Number) row[4]).intValue())
                .achievedTime(row[5].toString())
                .build()
        ).collect(Collectors.toList());
    }
}