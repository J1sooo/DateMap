package com.est.back.chatroom;

import com.est.back.chatroom.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
    Optional<Chatroom> findByPartnerId(Long partnerId);
}