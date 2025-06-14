package com.est.back.match;

import com.est.back.match.dto.MatchChatMessageDto;
import com.est.back.match.service.MatchChatRoomService;
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
//        브라우저 종료/연결끊김 등 강제 퇴장시
//        유저가 속한 채팅방 찾아서  메시지 전송 & 방 삭제
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());


        String userId = (String) headerAccessor.getSessionAttributes().get("userId");
        if (userId == null) return;

        // 어떤 채팅방에 참여 중인지
        String chatRoomId = findChatRoomByUserId(userId);
        if (chatRoomId == null) return;

        // 메시지 알림 전송
        MatchChatMessageDto quitMessage = new MatchChatMessageDto();
        quitMessage.setChatRoomId(chatRoomId);
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

