package com.est.back.mypage;

import com.est.back.recommend.RecommendService;
import com.est.back.recommend.dto.RecommendResponseDto;
import org.springframework.stereotype.Controller;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import com.est.back.user.User;
import com.est.back.user.UserService;
import com.est.back.mypage.dto.MypagePartnerResponseDto;
import com.est.back.user.dto.UserInfoResponseDto;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MypageViewController {

    private final UserService userService;
    private final MypageService mypageService;
    private final RecommendService recommendService;

    @GetMapping("/mypage")
    public String viewMypage(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Long usn = user.getUsn();


        //프로필 정보
        UserInfoResponseDto userInfo = userService.getUserInfoDto(usn);
        model.addAttribute("userInfo", userInfo);

        //평가 받은 상대 정보
        List<MypagePartnerResponseDto> partners = mypageService.getMyPartners(usn);
        model.addAttribute("partnerList", partners);
        model.addAttribute("partnerCount", partners.size());
        //파트너 수
//        int feedbackCount = mypageService.getFeedbackCount(usn);
//        model.addAttribute("feedbackCount", feedbackCount);

        //내 코스 정보
        List<RecommendResponseDto> recommends = recommendService.getUserCourses(usn);
        model.addAttribute("recommendList", recommends);

        return "mypage";
    }
}
