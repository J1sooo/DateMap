package com.est.back.ranking.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankingDto {
    private Long usn;
    private String nickname;
    private String gender;
    private String profileImg;
    private Integer score;
    private String achievedTime; // 문자열로 포맷해서 응답
}