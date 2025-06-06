package com.est.back.recommend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "date_course")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Long courseId;

    @NonNull
    @Column(name = "usn", nullable = false)
    private Long usn;

    @NonNull
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @NonNull
    @Column(nullable = false, length = 50)
    private String title;

    @NonNull
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content1;

    @NonNull
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content2;

    @NonNull
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content3;

    @NonNull
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content4;

    @NonNull
    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    @NonNull
    @Column(nullable = false, length = 20)
    private String area;
}
