package com.est.back.blindChat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class AnalyzeDto {

    private final String analyze;
    private final String oneLiner;
    private final int score;
    private long count;

}

