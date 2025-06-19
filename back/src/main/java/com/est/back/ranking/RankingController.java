package com.est.back.ranking;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

import java.util.List;
import com.est.back.ranking.dto.RankingDto;

@RestController
@RequestMapping("/api/rankings/")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping
    public List<RankingDto> getRanking() {
        return rankingService.getRanking();
    }

    // 점수 랭킹
    @GetMapping("score")
    public List<RankingDto> getScoreRankingApi() {
        return rankingService.getRanking();
    }

    // 참여 횟수 랭킹
    @GetMapping("count")
    public List<RankingDto> getCountRankingApi() {
        return rankingService.getRankingByCount();
    }
}
