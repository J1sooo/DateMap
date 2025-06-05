package com.est.back.controller;

import com.est.back.domain.Chatroom;
import com.est.back.domain.Partner;
import com.est.back.service.ChatroomService;
import com.est.back.service.PartnerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chatroom")
public class ChatroomController {

    private final ChatroomService chatroomService;
    private final PartnerService partnerService;

    @Autowired
    public ChatroomController(ChatroomService chatroomService, PartnerService partnerService) {
        this.chatroomService = chatroomService;
        this.partnerService = partnerService;
    }

    @PostMapping
    public Chatroom createChatroom(@ModelAttribute Partner partner, HttpSession session) {
        //Long usn = (Long) session.getAttribute("usn"); // 세션에 저장된 사용자 usn
        Long usn = 1000L;// 임시 하드 코딩

        partner.setCreatedAt(LocalDateTime.now());
        Partner savedPartner = partnerService.savePartner(partner);

        Chatroom chatroom = Chatroom.builder()
                .usn(usn)
                .partnerId(savedPartner.getCharId())
                .build();

        return chatroomService.save(chatroom);
    }

    @GetMapping
    public List<Chatroom> getAllChatrooms() {
        return chatroomService.findAll();
    }

    @GetMapping("/{id}")
    public Chatroom getChatroomById(@PathVariable Long id) {
        return chatroomService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteChatroom(@PathVariable Long id) {
        chatroomService.deleteById(id);
    }
}
