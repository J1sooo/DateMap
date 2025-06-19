package com.est.back.blindChat.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Getter
@Setter
@Table(name= "blind_date_feedback")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlindDateFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 외래키 char_id
    @Column(name = "char_id", nullable = false)
    private Long charId;

    @Column(name = "usn", nullable = false)
    private Long usn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "summary", length = 1000)
    private String summary;

    @Column(name = "feedback", length = 2000)
    private String feedback;

    @Column(name = "score")
    private Integer score;


}
