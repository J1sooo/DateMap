package com.est.back.service;

import com.est.back.domain.Chatroom;
import com.est.back.repository.ChatroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatroomService {

    @Autowired
    private ChatroomRepository repository;

    public Chatroom save(Chatroom chatroom) {
        return repository.save(chatroom);
    }

    public List<Chatroom> findAll() {
        return repository.findAll();
    }

    public Chatroom findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}