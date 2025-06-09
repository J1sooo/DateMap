package com.est.back.blindChat.repository;

import com.est.back.blindChat.domain.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatMessageRepository extends JpaRepository<ChatMessage,Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtAsc(Long chatroomId);

    List<ChatMessage> findTop5ByChatRoomIdOrderByCreatedAtDesc(Long chatroomId);


    void deleteByChatRoomId(Long chatroomId);


}
