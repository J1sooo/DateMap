package com.est.back.user.dto;

import com.est.back.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
public class UserInfoResponseDto {

    private final String profileImg;
    private final String nickName;
    private final String email;
    private final String gender;
    private final String dateOfBirth;
    private final String preferArea;

    @Builder
    public UserInfoResponseDto(String profileImg, String nickName, String email,
                          String gender, String dateOfBirth, String preferArea) {
        this.profileImg = profileImg;
        this.nickName = nickName;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.preferArea = preferArea;
    }

    public static UserInfoResponseDto from(User user) {
        return UserInfoResponseDto.builder()
                .profileImg(user.getProfileImg())
                .nickName(user.getNickName())
                .email(user.getEmail())
                .gender(user.getGender().name()) // MALE or FEMALE
                .dateOfBirth(user.getDateOfBirth().toString()) // or formatted
                .preferArea(user.getPreferArea())
                .build();
    }
}
