package com.est.back.blindChat.controller;

import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.dto.AnalyzeDto;
import com.est.back.blindChat.dto.FeedbackDto;
import com.est.back.blindChat.dto.MessageDto;
import com.est.back.blindChat.service.BlindChatService;
import com.est.back.partner.PartnerRepository;
import com.est.back.partner.PartnerService;
import com.est.back.partner.domain.Partner;
import com.est.back.user.User;
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
    private final PartnerRepository partnerRepository;

    @GetMapping("/chat/{chatroomId}")
    public String showChatRoom(@PathVariable Long chatroomId, Model model , HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Long usn = user.getUsn();
        if (!blindChatService.isChatroomOwner(chatroomId, usn)) {
            return "redirect:/main";
        }
        List<ChatMessage> messages = blindChatService.getChatHistory(chatroomId);
        model.addAttribute("messages", messages.stream().map(MessageDto::from).toList());
        Partner partner = partnerRepository.findPartnerByChatroomId(chatroomId);

        model.addAttribute("imageUrl", partner.getImageUrl());
        model.addAttribute("partner", partner);
        model.addAttribute("chatroomId", chatroomId);
        return "chat";
    }

    @GetMapping("/chat/{chatroomId}/feedback")
    public String showFeedback(@PathVariable Long chatroomId, Model model,HttpSession session) {
        FeedbackDto dto = blindChatService.getFeedbackByChatroomId(chatroomId); // DB 조회
        User user = (User) session.getAttribute("loggedInUser");
        if(user == null){
            return "redirect:/login";
        }
        Long usn = user.getUsn();
        if (!blindChatService.isChatroomOwner(chatroomId, usn)) {
            return "redirect:/main";
        }
        model.addAttribute("feedback", dto);
        return "feedback"; // Thymeleaf 템플릿
    }

    @GetMapping("/analyze")
    public String analyze(Model model, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        Long usn = user.getUsn();
        if(usn == null){
            return "redirect:/login";
        }
        AnalyzeDto result = blindChatService.analyzeAllFeedbacksByUsn(usn);
        model.addAttribute("dto", result);
        System.out.println(result);
        System.out.println(result.getAnalyze() + result.getScore() + result.getOneLiner());
        return "analyze";
    }
}
