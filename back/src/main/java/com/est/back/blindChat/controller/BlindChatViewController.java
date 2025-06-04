package com.est.back.blindChat.controller;

import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.dto.MessageDto;
import com.est.back.blindChat.service.BlindChatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api")
public class BlindChatViewController {

    private final BlindChatService blindChatService;


    @GetMapping("/chat/{chatroomId}")
    public String showChatRoom(@PathVariable Long chatroomId, Model model) {
        List<ChatMessage> messages = blindChatService.getChatHistory(chatroomId);
        model.addAttribute("chatroomId", chatroomId);
        model.addAttribute("messages", messages.stream().map(MessageDto::from).toList());
        return "chat";
    }

    @PostMapping("/chat/{chatroomId}")
    public String sendMessage(@PathVariable Long chatroomId, @RequestParam String message) {
        blindChatService.chatWithGemini(chatroomId, message);
        return "redirect:/api/chat/" + chatroomId;
    }

    @DeleteMapping("/chat/{chatroomId}")
    public String deleteMessage(@PathVariable Long chatroomId) {
        blindChatService.deleteChatRoom(chatroomId);
        return "redirect:/api/chat/" + chatroomId;
    }

    @PostMapping("/chat/{chatroomId}/feedback")
    public String blindDateFeedback(@PathVariable Long chatroomId) {

        blindChatService.feedbackFromGemini(chatroomId);
        return "redirect:/api/chat/" + chatroomId;
    }
}
