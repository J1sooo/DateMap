package com.est.back.partner.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blind_date_character")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Partner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "char_id")
    private Long charId;

    @NonNull
    @Column(nullable = false, length = 10)
    private String gender;

    @NonNull
    @Column(name = "age_group", nullable = false, length = 10)
    private String ageGroup;

    @NonNull
    @Column(name = "personal_type", nullable = false, length = 50)
    private String personalType;

    @NonNull
    @Column(nullable = false, length = 50)
    private String hobby;

    @NonNull
    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @NonNull
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
