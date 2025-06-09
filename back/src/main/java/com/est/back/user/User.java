package com.est.back.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usn", nullable = false)
    private Long usn;

    @Column(name = "user_id", nullable = false, unique = true, length = 50)
    private String userId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickName", nullable = false, unique = true, length = 8)
    private String nickName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 2)
    private Gender gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "prefer_area", nullable = false, length = 100)
    private String preferArea;

    @Nullable
    @Column(name = "prefer_area_detail", length = 100)
    private String preferAreaDetail;

    @CreatedDate
    @Column(name = "join_date", nullable = false, updatable = false)
    private LocalDateTime joinDate;

    @Nullable
    @Column(name = "profile_img", length = 255)
    private String profileImg;

    public enum Gender {
        MALE, FEMALE
    }

    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    public void updateUserInfo(String nickName, String email, Gender gender,
                               LocalDate dateOfBirth, String preferArea,
                               String preferAreaDetail) {
        this.nickName = nickName;
        this.email = email;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.preferArea = preferArea;
        this.preferAreaDetail = preferAreaDetail;
    }

    public void updateProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }
}