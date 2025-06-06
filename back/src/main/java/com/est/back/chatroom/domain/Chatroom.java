package com.est.back.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chatroom")
@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "usn", nullable = false)
    private Long usn;

    @NonNull
    @Column(name = "partner_id", nullable = false)
    private Long partnerId;
}