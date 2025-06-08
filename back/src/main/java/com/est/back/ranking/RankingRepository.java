package com.est.back.ranking;

import com.est.back.ranking.domain.Ranking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
    List<Ranking> findTop10ByOrderByScoreDescAchievedTimeAsc();
}