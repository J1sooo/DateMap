package com.est.back.blindChat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AnalyzeDto {

    private final String analyze;
    private final String oneLiner;
    private final int score;

}

