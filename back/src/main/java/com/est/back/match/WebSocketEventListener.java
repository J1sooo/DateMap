package com.est.back.match;

import com.est.back.match.dto.MatchChatMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final MatchChatRoomService chatRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());


        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId == null) return;

        // 어떤 채팅방에 참여 중인지
        String chatRoomId = findChatRoomByUserId(userId);
        if (chatRoomId == null) return;

        // 메시지 알림 전송
        MatchChatMessageDto quitMessage = new MatchChatMessageDto();
        quitMessage.setChatRoomId(chatRoomId);
        quitMessage.setMessage(userId + "님이 연결을 종료했습니다.");
        quitMessage.setType(MatchChatMessageDto.MessageType.QUIT);

        messagingTemplate.convertAndSend("/topic/matchchat/" + chatRoomId, quitMessage);

        chatRoomService.removeChatRoom(chatRoomId);
    }

    private String findChatRoomByUserId(String userId) {
        Map<String, Map<String, String>> chatRooms = chatRoomService.getActiveChatRooms();
        for (Map.Entry<String, Map<String, String>> entry : chatRooms.entrySet()) {
            if (entry.getValue().containsKey(userId)) {
                return entry.getKey();
            }
        }
        return null;
    }
}

