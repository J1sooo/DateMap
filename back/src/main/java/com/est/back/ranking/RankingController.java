package com.est.back.ranking;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.RequiredArgsConstructor;

import java.util.List;
import com.est.back.ranking.dto.RankingDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking")
    public List<RankingDto> getRanking() {
        return rankingService.getRanking();
    }

}
