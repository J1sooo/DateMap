package com.est.back.recommend.dto;

import com.est.back.recommend.domain.Recommend;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendResponseDto {
    private Long courseId;
    private String imageUrl;
    private String title;
    private String content1;
    private String content2;
    private String content3;
    private String content4;
    private String area;

    public RecommendResponseDto(Recommend recommend) {
        this.courseId = recommend.getCourseId();
        this.imageUrl = recommend.getImageUrl();
        this.title = recommend.getTitle();
        this.content1 = recommend.getContent1();
        this.content2 = recommend.getContent2();
        this.content3 = recommend.getContent3();
        this.content4 = recommend.getContent4();
        this.area = recommend.getArea();
    }

    public static RecommendResponseDto fromRecommend(Recommend recommend) {
        return new RecommendResponseDto(recommend);
    }
}
