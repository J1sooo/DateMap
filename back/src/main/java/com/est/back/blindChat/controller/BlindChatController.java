package com.est.back.blindChat.controller;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.dto.FeedbackDto;
import com.est.back.blindChat.dto.MessageDto;
import com.est.back.blindChat.service.BlindChatService;
import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
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
public class BlindChatController {

    private final BlindChatService blindChatService;




    @PostMapping("/chat/{chatroomId}")
    public String sendMessage(@PathVariable Long chatroomId, @RequestParam String message) {
        blindChatService.chatWithGemini(chatroomId, message);
        return "redirect:/chat/" + chatroomId;
    }

    @DeleteMapping("/chat/{chatroomId}")
    public String deleteMessage(@PathVariable Long chatroomId) {
        blindChatService.deleteChatRoom(chatroomId);
        return "redirect:/api/chat/" + chatroomId;
    }

    @PostMapping("/chat/{chatroomId}/feedback")
    public String createFeedback(@PathVariable Long chatroomId , HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Long usn = user.getUsn();
        blindChatService.feedbackFromGemini(chatroomId , usn); // Gemini 호출 + 저장
        return "redirect:/chat/" + chatroomId + "/feedback"; // 결과 보기 페이지로 리다이렉트
    }

}
