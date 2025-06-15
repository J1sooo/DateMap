package com.est.back.match.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationMessageDto {
    public enum NotificationType {
        CHAT_MESSAGE,
        MATCH_FOUND,
        GENERAL
    }

    private NotificationType type;
    private String senderNickname;
    private String recipientUserId;
    private String message;
    private String link;
    private String timestamp;
    private boolean read;
    private String chatRoomId;
}
