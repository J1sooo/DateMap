package com.est.back.alan;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AlanController {
    private final AlanService alanService;

    @GetMapping("/alan")
    public ResponseEntity<String> getAlan(@RequestParam String content) {
        String res = alanService.AlanAiResponse(content);
        return ResponseEntity.ok(res);
    }
}
