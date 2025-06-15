package com.est.back.ranking.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ranking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usn;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(nullable = false, length = 10)
    private String gender;

    @Column(name = "profile_img", nullable = false, length = 255)
    private String profileImg;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "achieved_time", nullable = false)
    private LocalDateTime achievedTime;
}