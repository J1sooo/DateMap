package com.est.back.blindChat.controller;

import com.est.back.blindChat.service.BlindChatService;
import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/chatrooms")
public class BlindChatController {

    private final BlindChatService blindChatService;


    @PostMapping("/{chatroomId}")
    public String sendMessage(@PathVariable Long chatroomId, @RequestParam String message , HttpSession session) {
        blindChatService.chatWithGemini(chatroomId, message);

        return "redirect:/chatrooms/" + chatroomId;
    }

    @DeleteMapping("/{chatroomId}")
    public String deleteMessage(@PathVariable Long chatroomId) {
        blindChatService.deleteChatRoom(chatroomId);
        return "redirect:/api/chatrooms/" + chatroomId;
    }

    @PostMapping("/{chatroomId}/feedback")
    public String createFeedback(@PathVariable Long chatroomId , HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Long usn = user.getUsn();
        if (!blindChatService.isChatroomOwner(chatroomId, usn)) {
            return "redirect:/main";
        }
        blindChatService.feedbackFromGemini(chatroomId , usn); // Gemini 호출 + 저장
        return "redirect:/chatrooms/" + chatroomId + "/feedback"; // 결과 보기 페이지로 리다이렉트
    }

}
