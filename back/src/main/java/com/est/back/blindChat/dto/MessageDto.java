package com.est.back.blindChat.dto;

import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.domain.Role;
import lombok.Data;
import lombok.Getter;

@Data
public class MessageDto {
    private Role role;
    private String message;

    public MessageDto(Role role, String message) {
        this.role = role;
        this.message = message;
    }

    public static MessageDto from(ChatMessage entity) {
        return new MessageDto(entity.getRole(), entity.getMessage());
    }
}


