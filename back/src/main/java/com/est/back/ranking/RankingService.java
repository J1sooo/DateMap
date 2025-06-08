package com.est.back.ranking;

import com.est.back.ranking.domain.Ranking;
import com.est.back.ranking.dto.RankingDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    private final RankingRepository repository;

    @PersistenceContext
    private EntityManager em;

    public RankingService(RankingRepository repository) {
        this.repository = repository;
    }

    public List<RankingDto> getTop10() {
        return repository.findTop10ByOrderByScoreDescAchievedTimeAsc()
                .stream()
                .map(r -> RankingDto.builder()
                        .usn(r.getUsn())
                        .nickname(r.getNickname())
                        .gender(r.getGender())
                        .profileImg(r.getProfileImg())
                        .score(r.getScore())
                        .achievedTime(r.getAchievedTime().toString())
                        .build())
                .collect(Collectors.toList());
    }

    public void overwriteRankings(List<Ranking> newRankings) {
        repository.deleteAllInBatch();
        repository.saveAll(newRankings);
    }

    // 주간 랭킹 갱신 로직 (스케줄러에서 호출)
    public List<Ranking> calculateWeeklyTop10() {
        String query = """
            SELECT u.usn, u.nickname, u.gender, u.profileImg, f.score, MIN(f.createdAt) as achievedTime
            FROM BlindDateFeedback f
            JOIN User u ON f.usn = u.usn
            WHERE f.createdAt >= :oneWeekAgo
            GROUP BY u.usn, u.nickname, u.gender, u.profileImg, f.score
            HAVING f.score = MAX(f.score)
            ORDER BY f.score DESC, achievedTime ASC
        """;

        List<Object[]> result = em.createQuery(query, Object[].class)
                .setParameter("oneWeekAgo", LocalDateTime.now().minusDays(7))
                .setMaxResults(10)
                .getResultList();

        return result.stream().map(row -> Ranking.builder()
                        .usn((Long) row[0])
                        .nickname((String) row[1])
                        .gender((String) row[2])
                        .profileImg((String) row[3])
                        .score((Integer) row[4])
                        .achievedTime((LocalDateTime) row[5])
                        .build())
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 자정에 실행
    @Transactional
    public void updateWeeklyRanking() {
        List<Ranking> top10 = calculateWeeklyTop10();

        // 유효한 usn만 필터링
        List<Long> validUsns = em.createQuery("SELECT u.usn FROM User u", Long.class).getResultList();

        List<Ranking> filtered = top10.stream()
                .filter(r -> validUsns.contains(r.getUsn()))
                .collect(Collectors.toList());
        //todo 랭킹 산정 시 회원 탈되된 랭커는 어떻게 처리?

        overwriteRankings(filtered);

    }
}