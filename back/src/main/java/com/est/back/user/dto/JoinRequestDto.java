package com.est.back.user.dto;

import com.est.back.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;
import java.time.LocalDate;

@Getter
@Setter
public class JoinRequestDto {
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 6, max = 12, message = "아이디는 6~12자 이내로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영문과 숫자만 가능합니다.")
    private String userId;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8~20자 이내로 입력해주세요.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}$",
            message = "비밀번호는 영문과 숫자를 포함하여 8~20자 이내로 입력해주세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 값입니다.")
    private String passwordCheck;

    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 1, max = 8, message = "닉네임은 8자 이내로 입력해주세요.")
    private String nickName;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotNull(message = "성별은 필수 선택 값입니다.")
    private User.Gender gender;

    @NotNull(message = "출생년도는 필수 입력 값입니다.")
    private Integer birthYear;
    @NotNull(message = "출생월은 필수 입력 값입니다.")
    private Integer birthMonth;
    @NotNull(message = "출생일은 필수 입력 값입니다.")
    private Integer birthDay;

    private String preferArea; //첫번째 선호지역

    @Nullable
    private String preferAreaDetail; //두번째 선호지역

    @NotNull(message = "이용약관 동의는 필수입니다.")
    private Boolean termsAgreed;

    @NotNull(message = "개인정보 취급방침 동의는 필수입니다.")
    private Boolean privacyAgreed;

    private Boolean allAgreed; // 모두 동의

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