package com.est.back.chatroom.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatroomDto {
    private Long id;
    private Long usn;
    private Long partnerId;
}
