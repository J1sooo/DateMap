package com.est.back.blindChat.controller;

import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.dto.AnalyzeDto;
import com.est.back.blindChat.dto.FeedbackDto;
import com.est.back.blindChat.dto.MessageDto;
import com.est.back.blindChat.service.BlindChatService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RequiredArgsConstructor
@Controller
public class BlindChatViewController {

    private final BlindChatService blindChatService;

    @GetMapping("/chat/{chatroomId}")
    public String showChatRoom(@PathVariable Long chatroomId, Model model) {
        List<ChatMessage> messages = blindChatService.getChatHistory(chatroomId);
        model.addAttribute("messages", messages.stream().map(MessageDto::from).toList());
        return "chat";
    }

    @GetMapping("/chat/{chatroomId}/feedback")
    public String showFeedback(@PathVariable Long chatroomId, Model model) {
        FeedbackDto dto = blindChatService.getFeedbackByChatroomId(chatroomId); // DB 조회
        model.addAttribute("feedback", dto);
        return "feedback"; // Thymeleaf 템플릿
    }

    @GetMapping("/analyze")
    public String analyze(Model model, HttpSession session) {
//        Long usn = (Long) session.getAttribute("usn");
        Long usn = 1L;
        AnalyzeDto result = blindChatService.analyzeAllFeedbacksByUsn(usn);
        model.addAttribute("dto", result);
        return "analyze";
    }
}
