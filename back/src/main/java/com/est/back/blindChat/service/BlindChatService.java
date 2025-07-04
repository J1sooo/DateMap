package com.est.back.blindChat.service;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.domain.ChatRoom;
import com.est.back.blindChat.domain.Role;
import com.est.back.blindChat.dto.AnalyzeDto;
import com.est.back.blindChat.dto.FeedbackDto;
import com.est.back.blindChat.repository.BlindDateFeedbackRepository;
import com.est.back.blindChat.repository.ChatMessageRepository;
import com.est.back.blindChat.repository.ChatRoomRepository;
import com.est.back.chatroom.ChatroomRepository;
import com.est.back.chatroom.domain.Chatroom;
import com.est.back.partner.PartnerRepository;
import com.est.back.partner.domain.Partner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindChatService {

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient = WebClient.create();
    private final ChatRoomRepository chatRoomRepository;
    private final ChatroomRepository chatroomRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BlindDateFeedbackRepository blindDateFeedbackRepository;
    private final PartnerRepository partnerRepository;


    //대화 내용 불러오기
    @Transactional
    public List<ChatMessage> getChatHistory(Long chatroomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatroomId);
    }

    public boolean isChatroomOwner(Long chatroomId, Long usn) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findById(chatroomId);
        return chatRoomOpt.isPresent() && chatRoomOpt.get().getUsn().equals(usn);
    }


    //사용자가 보낸 메시지 와 Gemini API의 응답을 DB에 저장
    @Transactional
    public void chatWithGemini(Long chatroomId, String userMessage) {
        Partner partner = partnerRepository.findPartnerByChatroomId(chatroomId);

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
        String reply = sendToGemini(chatroomId,
            "이건 너가 맡은 소개팅 상황극의 역할이고\n"
                + "성별: " + (partner.getGender().equals("male") ? "남성" : "여성") + "\n"
                + "나이대: " + partner.getAgeGroup() + "\n"
                + "성격: " + partner.getPersonalType() + "\n"
                + "취미: " + partner.getHobby() + "\n\n"
                + "대답할 때는 너의 정보를 고려해서 답변해줘.\n"
                + "만약 추가적인 정보가 필요한 경우 실제 정보가 없어도 너 스스로 상상해서 채워줘.\n\n"
                + "그리고 우리가 전에 나눴던 대화들을 이용해서 흐름 유지하면서 5줄 이내로 대답해줘.\n"
                + userMessage
        );

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
            .findTop10ByChatRoomIdOrderByCreatedAtDesc(chatroomId);
        Collections.reverse(history); // 시간 순서로

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

        String url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + apiKey;

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
    public String extractTextFromResponse(String json) {
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
    public BlindDateFeedback feedbackFromGemini(Long chatroomId, Long usn) {

        List<Map<String, Object>> parts = getChatHistory(chatroomId).stream()
            .map(msg -> Map.of(
                "role", msg.getRole() == Role.AI ? "model" : "user",
                "parts", List.of(Map.of("text", msg.getMessage()))
            ))
            .collect(Collectors.toList());
        String prompt = """
            이전 소개팅 대화를 기반으로 아래 요청을 수행해줘.
            각 항목은 반드시 "1.", "2.", "3."으로 시작해줘.
            
            1. 전체 대화를 3줄로 요약해줘.
            2. 소개팅에서 user의 말투와 대답이 자연스러웠는지 평가해줘. 당신이라고 불러줘.
            3. user의 대화 스타일과 태도를 고려해 100점 만점으로 점수를 매겨줘. 점수만 알려줘.
            답변은 3줄 이내로 간결하게 해주고 user라는 단어 말고 내 이름으로 말해줘.
            """;

        parts.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", prompt))
        ));

        String url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + apiKey;

        Map<String, Object> body = Map.of("contents", parts);

        String responseJson = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        String feedbackText = extractTextFromResponse(responseJson);

        String[] part = feedbackText.split("(?m)^\\s*\\d+\\.");  // "1.", "2.", "3."으로 자름

        String summary = part.length > 1 ? part[1].trim() : "";
        String feedback = part.length > 2 ? part[2].trim() : "";
        String scoreStr = part.length > 3 ? part[3].replaceAll("[^0-9]", "") : "0";
        int score = Integer.parseInt(scoreStr);
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
            .orElseThrow(() -> new RuntimeException("Chatroom not found"));

        Long charId = chatroom.getPartnerId();

        BlindDateFeedback feedbackEntity = new BlindDateFeedback();

        feedbackEntity.setCharId(charId);
        feedbackEntity.setUsn(usn);
        feedbackEntity.setCreatedAt(LocalDateTime.now());
        feedbackEntity.setSummary(summary);
        feedbackEntity.setFeedback(feedback);
        feedbackEntity.setScore(score);

        blindDateFeedbackRepository.save(feedbackEntity);
        return feedbackEntity;

    }

    public FeedbackDto getFeedbackByChatroomId(Long chatroomId) {
        ChatRoom chatroom = chatRoomRepository.findById(chatroomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방이 존재하지 않습니다."));

        Long partnerId = chatroom.getPartnerId();

        BlindDateFeedback feedback = blindDateFeedbackRepository.findByCharId(partnerId)
            .orElseThrow(() -> new IllegalArgumentException("해당 캐릭터의 피드백이 없습니다."));

        String formattedSummary = feedback.getSummary().replaceAll("\\.\\s*", ".<br/>")
            .replaceAll(",\\s*", ",<br/>");
        String formattedFeedback = feedback.getFeedback().replaceAll("\\.\\s*", ".<br/>")
            .replaceAll(",\\s*", ",<br/>");

        return new FeedbackDto(formattedSummary, feedback.getScore(), formattedFeedback);
    }

    private String sendPromptToGemini(String prompt) {
        String url =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                + apiKey;

        Map<String, Object> body = Map.of(
            "contents", List.of(
                Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", prompt))
                )
            )
        );

        String responseJson = webClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return extractTextFromResponse(responseJson);  // 기존 응답 파싱 메서드 사용
    }


    @Transactional
    public AnalyzeDto analyzeAllFeedbacksByUsn(Long usn) {
        List<BlindDateFeedback> feedbacks = blindDateFeedbackRepository.findAllByUsn(usn);
        if (feedbacks.isEmpty()) {
            throw new IllegalArgumentException("해당 유저의 피드백이 없습니다.");
        }

        String combined = feedbacks.stream()
            .map(f -> String.format("요약: %s\n피드백: %s\n점수: %d", f.getSummary(), f.getFeedback(),
                f.getScore()))
            .collect(Collectors.joining("\n\n"));

        String prompt = """
            이전에 사용자가 받은 여러 소개팅 피드백을 기반으로 아래 요청을 수행해줘.
            1. 전반적인 대화 성향, 장점과 단점, 개선점 등을 종합적으로 분석해서 3줄이내로 평가해줘
            2. 전반적인 대화 성향, 장점과 단점, 개선점 등을 분석한 것을 기준으로 소개팅 점수 100점 만점으로 점수를 매겨줘, 점수만 알려주면 돼.
            3. 점수와 분석한 평가를 통한 한줄평가 부탁해
            모든 답변은 자연스럽게 해줘
            """;

        // Gemini API 요청 로직 호출
        String result = sendPromptToGemini(prompt + combined);
        long count = blindDateFeedbackRepository.countByUsn(usn);
        return extractAnalysisFromResponse(count, result);
    }

    private AnalyzeDto extractAnalysisFromResponse(long count, String responseText) {

        String[] parts = responseText.split("(?m)^\\s*\\d+\\.");

        String analyze = parts.length > 1
            ? parts[1].replaceAll("\\.\\s*", ".<br/>").replaceAll(",\\s*", ",<br/>").trim()
            : "";

        String rawScore = parts.length > 2 ? parts[2].trim() : "0점";
        String scoreStr = rawScore.replaceAll("[^0-9]", "");
        int score = Integer.parseInt(scoreStr);

        String oneLiner = parts.length > 3 ? parts[3].trim() : "";
        return AnalyzeDto.builder()
            .analyze(analyze)
            .oneLiner(oneLiner)
            .score(score)
            .count(count)
            .build();
    }


}



