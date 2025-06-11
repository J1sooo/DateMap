package com.est.back.chatroom;

import com.est.back.chatroom.domain.Chatroom;
import com.est.back.chatroom.dto.ChatroomDto;
import com.est.back.chatroom.dto.ChatroomResponseDto;
import com.est.back.partner.domain.Partner;
import com.est.back.partner.PartnerService;
import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@Controller
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
    public String createChatroom(@ModelAttribute Partner partner,HttpSession session) {

        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Long usn = user.getUsn();
        String nickName = user.getNickName();

        // 1. Partner 저장
        partner.setCreatedAt(LocalDateTime.now());
        Partner savedPartner = partnerService.savePartner(partner);

        // 2. Chatroom 생성
        Chatroom chatroom = Chatroom.builder()
            .usn(usn)
            .partnerId(savedPartner.getCharId())
            .build();
        ChatroomDto chatroomDto = chatroomService.save(chatroom);

        // 4. Gemini에게 첫 메시지 전송 → chatMessage 저장
        chatroomService.firstSendToGemini(chatroomDto.getId(), savedPartner, nickName);
        System.out.println("받은 imageUrl = " + partner.getImageUrl());
        System.out.println(partner.getPersonalType());
        System.out.println(partner.getHobby());
        // 5. chatroomId 반환
        return "redirect:/chat/" + chatroomDto.getId();
    }


    @GetMapping
    public List<ChatroomDto> getAllChatrooms() {
        return chatroomService.findAll();
    }

    @GetMapping("/{id}")
    public ChatroomDto getChatroomById(@PathVariable Long id) {
        return chatroomService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteChatroom(@PathVariable Long id) {
        chatroomService.deleteById(id);
        // todo delete partner
    }
}
