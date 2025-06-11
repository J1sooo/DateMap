package com.est.back.mypage.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MypagePartnerResponseDto {
    private Long id;//feedbackId
    private Long chatId;
    private String summary;
    private String feedback;
    private Integer score;
    private String imageUrl;
}
