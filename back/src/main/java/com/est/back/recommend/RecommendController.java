package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import com.est.back.recommend.dto.RecommendResponseDto;
import com.est.back.s3.ImageUploadService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {
    private final RecommendService recommendService;
    private final ImageUploadService imageUploadService;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    // POST /api/recommend
    @PostMapping
    public Recommend createRecommend(@ModelAttribute Recommend recommend,
                                     @RequestParam(value = "image", required = false) MultipartFile image,
                                     HttpSession session) throws IOException {
        //Long usn = (Long) session.getAttribute("usn"); // 세션에 저장된 사용자 usn
        Long usn = 1000L;// 임시 하드 코딩
        recommend.setUsn(usn);
        recommend.setSavedAt(LocalDateTime.now()); // 저장 시간 자동 입력

        // 파일이 비어 있으면 기본 이미지 설정
        if (image == null || image.isEmpty()) {
            recommend.setImageUrl(String.format("https://%s.s3.ap-northeast-2.amazonaws.com/noImage.jpg", bucketName));
        } else {
            String imageUrl = imageUploadService.uploadFile(image);
            recommend.setImageUrl(imageUrl);
        }

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

    // Patch /api/recommend/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<RecommendResponseDto> updateRecommend(@PathVariable Long id, @RequestParam MultipartFile image) throws IOException {
        RecommendResponseDto responseDto = recommendService.updateImage(id, image);
        return ResponseEntity.ok(responseDto);
    }
}
