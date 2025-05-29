package com.est.back.blindChat.controller;

import com.est.back.blindChat.dto.MessageDto;
import com.est.back.blindChat.service.blindChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class blindChatController {

    private final blindChatService blindChatService;

    public blindChatController(blindChatService blindChatService) {
        this.blindChatService = blindChatService;
    }

    @GetMapping("/gemini")
    public ResponseEntity<String> testGemini(@RequestParam String message) {
        message = "안녕";
        String response = blindChatService.sendToGemini(message);
        return ResponseEntity.ok(response);
    }

}
