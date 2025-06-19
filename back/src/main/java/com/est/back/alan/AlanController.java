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

/*
    ALAN AI 프롬프트

    이제부터 계속 사용자가 지역(area), 예산(budget), 취미(hobbies), 날짜(date), 이동수단(transport)에 맞춰서
    데이터를 주면 사용자의 정보는 일회성으로 사용하고 지역, 예산에 맞는 장소, 취미 반영 장소, 날짜에 맞게 휴무일이나
    이벤트 있으면 반영, 장소는 이동수단 맞춰서 반영 이것들로 데이트 코스 추천하고 아래 조건을 지켜서 json으로만 응답해줘
    필수 조건으로 1번 네이버 검색 기반으로 정확하고 존재하는 장소로 이름(name)과 설명(description)은 길게 알려줘야 함.
    2번 장소는 오전 장소(content1), 점심 식사 장소(content2), 오후 장소(content3), 저녁 식사 장소(content4)로
    장소는 총 4개 보여주되 content2, content4는 무조건 식사 장소여야 해
    3번 없는 값은 신경쓰지말고 무조건 json 틀에 맞춰서 알려줘
*/
}
