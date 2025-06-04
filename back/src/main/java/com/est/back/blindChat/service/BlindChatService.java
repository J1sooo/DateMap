package com.est.back.blindChat.service;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.domain.ChatRoom;
import com.est.back.blindChat.domain.Role;
import com.est.back.blindChat.repository.BlindDateFeedbackRepository;
import com.est.back.blindChat.repository.ChatMessageRepository;
import com.est.back.blindChat.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

@Service
public class BlindChatService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient = WebClient.create();
    private final ChatRoomRepository chatRoomRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BlindDateFeedbackRepository blindDateFeedbackRepository;


    public BlindChatService(ChatMessageRepository chatMessageRepository,
        ChatRoomRepository chatRoomRepository , BlindDateFeedbackRepository blindDateFeedbackRepository) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.blindDateFeedbackRepository = blindDateFeedbackRepository;
    }

    //대화 내용 불러오기
    @Transactional
    public List<ChatMessage> getChatHistory(Long chatroomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatroomId);
    }

    //사용자가 보낸 메시지 와 Gemini API의 응답을 DB에 저장
    @Transactional
    public void chatWithGemini(Long chatroomId, String userMessage) {

        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다."));
        // 1. 사용자 메시지 저장
        ChatMessage userMsg = new ChatMessage();
        userMsg.setChatRoom(chatRoom);
        userMsg.setRole(Role.USER);
        userMsg.setMessage(userMessage);
        userMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(userMsg);

        // 2. Gemini 응답 받기
        String reply = sendToGemini(chatroomId, "우리가 전에 나눴던 대화들이야 흐름 유지 하면서 5줄 이내로 대답해줘" + userMessage); // 프롬프트 문구 추가

        // 3. 응답 저장
        ChatMessage modelMsg = new ChatMessage();
        modelMsg.setChatRoom(chatRoom);
        modelMsg.setRole(Role.AI);
        modelMsg.setMessage(reply);
        modelMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(modelMsg);
    }

    //Gemini API 호출
    @Transactional
    public String sendToGemini(Long chatroomId, String userMessage) {
        List<ChatMessage> history = chatMessageRepository
            .findTop5ByChatRoomIdOrderByCreatedAtDesc(chatroomId);
        Collections.reverse(history); // 시간 순서로 바꿔줘야 함

        List<Map<String, Object>> parts = history.stream()
            .map(msg -> Map.of(
                "role", msg.getRole() == Role.AI ? "model" : "user",
                "parts", List.of(Map.of("text", msg.getMessage()))
            ))
            .collect(Collectors.toList());

        // 현재 메시지 추가
        parts.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", userMessage))
        ));

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of("contents", parts);

        String responseJson = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return extractTextFromResponse(responseJson);
    }

    // Gemini API가 보낸 JSON 응답을 text 부분만 나오게 자르기
    private String extractTextFromResponse(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode textNode = root.path("candidates")
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text");

            if (textNode.isMissingNode()) {
                return "(text 노드 없음)";
            }
            return textNode.asText();
        } catch (Exception e) {
            e.printStackTrace();
            return "(JSON 파싱 실패)";
        }
    }
    @Transactional
    public void deleteChatRoom(Long chatroomId) {
        chatMessageRepository.deleteByChatRoomId(chatroomId); // 메시지 먼저 삭제
        chatRoomRepository.deleteById(chatroomId);            // 그 다음 채팅방 삭제
    }

    @Transactional
    public void feedbackFromGemini(Long chatroomId) {

        List<Map<String, Object>> parts = getChatHistory(chatroomId).stream()
            .map(msg -> Map.of(
                "role", msg.getRole() == Role.AI ? "model" : "user",
                "parts", List.of(Map.of("text", msg.getMessage()))
            ))
            .collect(Collectors.toList());
        String prompt = """
            이전에 나눈 대화를 기반으로 아래 요청을 수행해줘.
            1. 대화 내용을 3줄로 요약해줘.
            2. 대화 흐름이 자연스러웠는지 평가해주고 부족한 부분이 있다면 피드백해줘.
            3. 대화 스타일, 태도 , 대화 흐름 등을 고려해 100점 만점으로 점수를 매겨줘, 점수만 알려주면 돼.
            모든 답변은 3줄 이내로 자연스럽게 해줘
            """;

        parts.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", prompt))
        ));

        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of("contents", parts);

        String responseJson = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        System.out.println("Gemini 응답 원문: " + responseJson);

        String feedbackText = extractTextFromResponse(responseJson);

        String[] part = feedbackText.split("(?m)^\\s*\\d+\\.");  // "1.", "2.", "3."으로 자름

        String summary = part.length > 1 ? part[1].trim() : "";
        String feedback = part.length > 2 ? part[2].trim() : "";
        String scoreStr = part.length > 3 ? part[3].replaceAll("[^0-9]", "") : "0";
        int score = Integer.parseInt(scoreStr);


        BlindDateFeedback feedbackEntity = new BlindDateFeedback();
        feedbackEntity.setCharId(1L);
        feedbackEntity.setUsn(1L);
        feedbackEntity.setCreatedAt(LocalDateTime.now());
        feedbackEntity.setSummary(summary);
        feedbackEntity.setFeedback(feedback);
        feedbackEntity.setScore(score);


        blindDateFeedbackRepository.save(feedbackEntity);

    }


}
