package com.est.back.ranking;

import com.est.back.ranking.dto.RankingDto;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
public class RankingViewController {

    private final RankingService service;

    @Autowired
    public RankingViewController(RankingService service) { this.service = service; }

    @GetMapping("/ranking")
    public String viewRanking(Model model) {
        List<RankingDto> rankings = service.getRanking();
        model.addAttribute("rankingList", rankings);
        return "ranking";
    }
}