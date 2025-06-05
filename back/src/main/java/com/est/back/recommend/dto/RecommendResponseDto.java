package com.est.back.recommend.dto;

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
}
