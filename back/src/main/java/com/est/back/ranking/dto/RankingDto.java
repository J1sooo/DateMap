// com.est.back.ranking.dto.RankingDto
package com.est.back.ranking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RankingDto {
    private Long usn;
    private String nickname;
    private String gender;
    private String profileImg;
    private int score; // 점수
    private String achievedTime;
    private long count; // 소개팅 참여 횟수
}