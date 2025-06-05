package com.est.back.chatroom;

import com.est.back.chatroom.domain.Chatroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
}