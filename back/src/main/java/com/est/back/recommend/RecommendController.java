package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import com.est.back.recommend.dto.RecommendResponseDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    private final RecommendService recommendService;

    @Autowired
    public RecommendController(RecommendService recommendService) {
        this.recommendService = recommendService;
    }

    // POST /api/recommend
    @PostMapping
    public Recommend createRecommend(@ModelAttribute Recommend recommend, HttpSession session) {
        //Long usn = (Long) session.getAttribute("usn"); // 세션에 저장된 사용자 usn
        Long usn = 1000L;// 임시 하드 코딩
        recommend.setUsn(usn);
        recommend.setSavedAt(LocalDateTime.now()); // 저장 시간 자동 입력
        return recommendService.saveRecommend(recommend);
    }

    // GET /api/recommend
    @GetMapping
    public List<RecommendResponseDto> getAllRecommends() {
        return recommendService.getAllRecommends();
    }

    // GET /api/recommend/{id}
    @GetMapping("/{id}")
    public RecommendResponseDto getRecommend(@PathVariable Long id){
        return recommendService.getRecommendById(id);
    }

    // DELETE /api/recommend
    @DeleteMapping
    public void deleteAllRecommends() {
        recommendService.deleteAllRecommends();
    }

    // DELETE /api/recommend/{id}
    @DeleteMapping("/{id}")
    public void deleteRecommend(@PathVariable Long id) {
        recommendService.deleteRecommendById(id);
    }

}
