package com.est.back.user.dto;

import com.est.back.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
public class UserUpdateRequestDto {

    @NotNull
    private Long usn;

    private String userId;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 1, max = 8, message = "닉네임은 8자 이내로 입력해주세요.")
    private String nickName;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotNull(message = "성별은 필수 선택 값입니다.")
    private User.Gender gender;

    @NotNull(message = "년도는 필수 입력 값입니다.")
    private Integer birthYear;
    @NotNull(message = "월은 필수 입력 값입니다.")
    private Integer birthMonth;
    @NotNull(message = "일은 필수 입력 값입니다.")
    private Integer birthDay;

    private String preferArea;
    private String preferAreaDetail;

    private String newPassword;
    private String newPasswordCheck;
    private String currentPassword;

    private MultipartFile profileImageFile;
    private String profileImageUrl;

    public LocalDate getDateOfBirth() {
        if (birthYear != null && birthMonth != null && birthDay != null) {
            try {
                return LocalDate.of(birthYear, birthMonth, birthDay);
            } catch (java.time.DateTimeException e) {
                return null;
            }
        }
        return null;
    }
}