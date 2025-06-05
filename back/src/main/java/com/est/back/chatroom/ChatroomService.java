package com.est.back.chatroom;

import com.est.back.chatroom.domain.Chatroom;
import com.est.back.chatroom.dto.ChatroomDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatroomService {

    @Autowired
    private ChatroomRepository repository;

    private ChatroomDto toDto(Chatroom entity) {
        return ChatroomDto.builder()
                .id(entity.getId())
                .usn(entity.getUsn())
                .partnerId(entity.getPartnerId())
                .build();
    }

    public ChatroomDto save(Chatroom chatroom) {
        Chatroom saved = repository.save(chatroom);
        return toDto(saved);
    }

    public List<ChatroomDto> findAll() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ChatroomDto findById(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

}
