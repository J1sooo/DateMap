package com.est.back.main;

import com.est.back.partner.domain.Partner;
import com.est.back.partner.dto.PartnerResponseDto;
import com.est.back.recommend.domain.Recommend;
import com.est.back.partner.PartnerService;
import com.est.back.recommend.RecommendService;

import com.est.back.recommend.dto.RecommendRequestDto;
import com.est.back.recommend.dto.RecommendResponseDto;
import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
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
    public String viewMainPage(Model model, HttpSession session) {
        List<PartnerResponseDto> partnerList = partnerService.getAllPartners();

        if (partnerList.size() > 4) {
            partnerList = partnerList.subList(0, 4);
        }
        // 로그인한 사용자 정보
        User loggedInUser = (User) session.getAttribute("loggedInUser");

        List<RecommendResponseDto> recommendList;

        if (loggedInUser != null) {
            String preferArea = loggedInUser.getPreferArea();
            String preferAreaDetail = loggedInUser.getPreferAreaDetail();

            // 선호 지역 기반 추천 코스 조회
            recommendList = recommendService.getRecommendsByArea(preferArea, preferAreaDetail);
            System.out.println(preferArea + preferAreaDetail);
            for (RecommendResponseDto recommend : recommendList) {
                System.out.println(recommend);

            }
            // 해당 지역의 추천이 없다면 전체 최신순 조회
            if (recommendList.isEmpty()) {
                recommendList = recommendService.getRecentRecommends();
            }
        } else {
            // 로그인하지 않은 경우 전체 추천 코스 최신순으로 제공
            recommendList = recommendService.getRecentRecommends();
        }
        if (recommendList.size() > 4) {
            recommendList = recommendList.subList(0, 4);
        }

        model.addAttribute("partners", partnerList);
        model.addAttribute("recommends", recommendList);
        return "main";
    }
}
