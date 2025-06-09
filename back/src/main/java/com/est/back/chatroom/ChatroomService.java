package com.est.back.chatroom;

import com.est.back.blindChat.domain.ChatMessage;
import com.est.back.blindChat.domain.ChatRoom;
import com.est.back.blindChat.domain.Role;
import com.est.back.blindChat.repository.ChatMessageRepository;
import com.est.back.blindChat.repository.ChatRoomRepository;
import com.est.back.chatroom.domain.Chatroom;
import com.est.back.chatroom.dto.ChatroomDto;
import com.est.back.partner.domain.Partner;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.est.back.blindChat.service.BlindChatService;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class ChatroomService {

    private final ChatRoomRepository chatRoomRepository;
    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final ChatroomRepository repository;
    private final ChatMessageRepository chatMessageRepository;
    private final WebClient webClient = WebClient.create();
    private final BlindChatService blindChatService;


    private ChatroomDto toDto(Chatroom entity) {
        return ChatroomDto.builder()
            .id(entity.getId())
            .usn(entity.getUsn())
            .partnerId(entity.getPartnerId())
            .build();
    }

    public ChatroomDto save(Chatroom chatroom) {
        Chatroom saved = repository.save(chatroom);
        return toDto(saved);
    }

    public List<ChatroomDto> findAll() {
        return repository.findAll().stream()
            .map(this::toDto)
            .collect(Collectors.toList());
    }

    public ChatroomDto findById(Long id) {
        return repository.findById(id)
            .map(this::toDto)
            .orElse(null);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void firstSendToGemini(Long chatroomId, Partner partner) {
        // 캐릭터 설명을 Gemini에 전달할 user 프롬프트로 구성
        String introPrompt = generateIntroPrompt(partner);

        List<Map<String, Object>> parts = new ArrayList<>();

        parts.add(Map.of(
            "role", "user",
            "parts", List.of(Map.of("text", introPrompt))
        ));

        // 2. WebClient 요청
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

        ChatRoom chatRoom = chatRoomRepository.findById(chatroomId)
            .orElseThrow(() -> new IllegalArgumentException("채팅방 없음"));

        // 3. 응답 파싱
        String aiMessage = blindChatService.extractTextFromResponse(responseJson);

        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setRole(Role.AI);
        message.setMessage(aiMessage);
        message.setCreatedAt(LocalDateTime.now());
        chatMessageRepository.save(message);
    }


    private String generateIntroPrompt(Partner partner) {
        return String.format(
            "지금부터 소개팅 상황극을 시작할거야. 너의 역할은 다음과 같아:\n\n" +
                "- 성별: %s\n" +
                "- 나이대: %s\n" +
                "- 성격: %s\n" +
                "- 취미: %s\n\n" +
                "이 역할을 바탕으로 소개팅 첫 대사를 자연스럽게, 호감 있게 시작해줘.",
            partner.getGender(),
            partner.getAgeGroup(),
            partner.getPersonalType(),
            partner.getHobby()
        );
    }
}
