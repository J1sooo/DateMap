package com.est.back.blindChat.dto;


import com.est.back.blindChat.domain.BlindDateFeedback;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FeedbackDto {

    private String summary;
    private Integer score;
    private String feedback;


    public static FeedbackDto from(BlindDateFeedback entity) {
        return new FeedbackDto(entity.getSummary(), entity.getScore(), entity.getFeedback());
    }
}
