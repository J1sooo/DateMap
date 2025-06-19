package com.est.back.recommend;

import com.est.back.recommend.dto.RecommendResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Controller
public class RecommendViewController {
    private final RecommendService recommendService;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    @GetMapping("/recommendations/{id}")
    public String recommendPlaceDetail(@PathVariable Long id, Model model) throws JsonProcessingException {
        RecommendResponseDto recommend = recommendService.getRecommendById(id);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, String>> contents = List.of(
                mapper.readValue(recommend.getContent1(), new TypeReference<>() {}),
                mapper.readValue(recommend.getContent2(), new TypeReference<>() {}),
                mapper.readValue(recommend.getContent3(), new TypeReference<>() {}),
                mapper.readValue(recommend.getContent4(), new TypeReference<>() {})
        );
        model.addAttribute("title", recommend.getTitle());
        model.addAttribute("area", recommend.getArea());
        String imageUrl = recommend.getImageUrl().equals(
                String.format("https://%s.s3.ap-northeast-2.amazonaws.com/noImage.jpg", bucketName))
                ? "" : recommend.getImageUrl();
        model.addAttribute("imageUrl", imageUrl);
        model.addAttribute("labels", List.of("오전 장소", "점심 식사", "오후 장소", "저녁 식사"));
        model.addAttribute("contents", contents);
        return "recommendPlaceDetail";
    }
}
