package com.est.back.chatroom.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomResponseDto {
    private Long chatroomId;
    private String message;
}
