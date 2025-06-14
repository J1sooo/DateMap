package com.est.back.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchChatMessageDto {      // 채팅 메시지 한 건

    public enum MessageType {
        ENTER, TALK, QUIT
    }

    private MessageType type;
    private String chatRoomId;
    private String senderNickname;
    private String senderUserId;
    private String message;
    private String timestamp;
}