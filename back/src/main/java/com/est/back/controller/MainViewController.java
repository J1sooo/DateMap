package com.est.back.controller;

import com.est.back.domain.Partner;
import com.est.back.domain.Recommend;
import com.est.back.service.PartnerService;
import com.est.back.service.RecommendService;

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
        List<Partner> partnerList = partnerService.getAllPartners();
        List<Recommend> recommendList = recommendService.getAllRecommends();


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

















