package com.est.back.recommend;

import com.est.back.recommend.dto.RecommendResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class RecommendViewController {

    @GetMapping("/recommend-setting")
    public String viewPartnerSetting() {

        return "recommendSetting";
    }

    private final RecommendService recommendService;

    @GetMapping("/recommendplace/{id}")
    public String viewRecommendPlaceDetail(@PathVariable Long id, Model model) {
        RecommendResponseDto dto = recommendService.getRecommendById(id);
        model.addAttribute("recommendPlace", dto);
        return "recommendPlaceDetail";
    }

}
