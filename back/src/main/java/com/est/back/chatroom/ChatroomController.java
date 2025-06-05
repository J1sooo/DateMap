package com.est.back.chatroom;

import com.est.back.chatroom.domain.Chatroom;
import com.est.back.chatroom.dto.ChatroomDto;
import com.est.back.chatroom.dto.ChatroomResponseDto;
import com.est.back.partner.domain.Partner;
import com.est.back.partner.PartnerService;
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
    public ChatroomResponseDto createChatroom(@ModelAttribute Partner partner, HttpSession session) {
        //Long usn = (Long) session.getAttribute("usn");
        Long usn = 1000L; // 임시 하드 코딩

        partner.setCreatedAt(LocalDateTime.now());
        Partner savedPartner = partnerService.savePartner(partner);
        //todo: Add transaction process when partner creation fails

        Chatroom chatroom = Chatroom.builder()
                .usn(usn)
                .partnerId(savedPartner.getCharId())
                .build();

        ChatroomDto chatroomDto = chatroomService.save(chatroom);

        return ChatroomResponseDto.builder()
                .message("채팅 시작")
                .chatroomId(chatroomDto.getId())
                .build();
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
