package com.est.back.partner.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponseDto {
    private String imageUrl;
    private String gender;
    private String ageGroup;
    private String personalType;
    private String hobby;
}
