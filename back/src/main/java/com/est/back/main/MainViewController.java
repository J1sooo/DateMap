package com.est.back.main;

import com.est.back.partner.domain.Partner;
import com.est.back.partner.dto.PartnerResponseDto;
import com.est.back.recommend.domain.Recommend;
import com.est.back.partner.PartnerService;
import com.est.back.recommend.RecommendService;

import com.est.back.recommend.dto.RecommendRequestDto;
import com.est.back.recommend.dto.RecommendResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainViewController {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private RecommendService recommendService;

    @GetMapping("/main")
    public String viewMainPage(Model model) {
        List<PartnerResponseDto> partnerList = partnerService.getAllPartners();
        List<RecommendResponseDto> recommendList = recommendService.getAllRecommends();


        // 일단 4개만. 임시 코드
        if (partnerList.size() > 4) {
            partnerList = partnerList.subList(0, 4);
        }

        if (recommendList.size() > 4) {
            recommendList = recommendList.subList(0, 4);
        }

        model.addAttribute("partners", partnerList);
        model.addAttribute("recommends", recommendList);

        return "main";  // => main.html
    }


}

















