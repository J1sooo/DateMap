package com.est.back.match;

import com.est.back.match.dto.MatchChatMessageDto;
import com.est.back.match.dto.NotificationMessageDto;
import com.est.back.match.service.MatchChatRoomService;
import com.est.back.match.service.NotificationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.context.event.EventListener;
import java.util.Map;
import java.util.Objects;

@Controller
public class WebSocketChatHandler {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final MatchChatRoomService chatRoomService;

    public WebSocketChatHandler(SimpMessagingTemplate messagingTemplate, NotificationService notificationService, MatchChatRoomService chatRoomService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.chatRoomService = chatRoomService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload MatchChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = chatMessage.getChatRoomId();
        String senderNickname = chatMessage.getSenderNickname();
        String senderUserId = (String) headerAccessor.getSessionAttributes().get("userId");

        // 채팅방에 메시지 전송
        messagingTemplate.convertAndSend("/topic/matchchat/" + chatRoomId, chatMessage);

        // 상대방에게 알림 전송
        Map<String, String> participants = chatRoomService.getParticipants(chatRoomId);
        if (participants != null) {
            String recipientUserId = null;
            for (Map.Entry<String, String> entry : participants.entrySet()) {
                if (!entry.getKey().equals(senderUserId)) {
                    recipientUserId = entry.getKey();
                    break;
                }
            }

            if (recipientUserId != null) {

                notificationService.sendAndStoreNotification(
                        recipientUserId,
                        NotificationMessageDto.NotificationType.CHAT_MESSAGE,
                        senderNickname,
                        chatMessage.getMessage(),
                        "/matchchat/room/" + chatRoomId,
                        chatRoomId

                );
                System.out.println("🔗 링크 확인:/matchchat/room/" + chatRoomId);

            }
        }
    }

    @MessageMapping("/chat.addUser")
    public void addUser(@Payload MatchChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String chatRoomId = chatMessage.getChatRoomId();
        String nickname = chatMessage.getSenderNickname();
        String userId = chatMessage.getSenderUserId();

        // 세션에 사용자 정보 저장
        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("nickname", nickname);
        headerAccessor.getSessionAttributes().put("roomId", chatRoomId);
        headerAccessor.getSessionAttributes().put("userId", userId);

        // 채팅방에 입장 메시지 전송
        chatMessage.setType(MatchChatMessageDto.MessageType.ENTER);
        chatMessage.setMessage(nickname + " 님이 입장했습니다.");
        messagingTemplate.convertAndSend("/topic/matchchat/" + chatRoomId, chatMessage);

        notificationService.markChatNotificationsAsRead(userId, chatRoomId);
    }

    @MessageMapping("/chat.quitRoom")
    public void quitRoom(@Payload MatchChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String senderNickname = chatMessage.getSenderNickname();
        String chatRoomId = chatMessage.getChatRoomId();
        String senderUserId = (String) headerAccessor.getSessionAttributes().get("userId");

        Map<String, String> participants = chatRoomService.getParticipants(chatRoomId);
        if (participants != null) {
            // 현재 나가는 사용자의 ID를 participants 맵에서 제거
            participants.remove(senderUserId);

            // 나가는 사용자 외에 다른 참가자가 남아있는지 확인
            if (!participants.isEmpty()) {
                // 남아있는 다른 참가자에게만 나감 메시지 전송
                chatMessage.setSenderNickname("System");
                chatMessage.setType(MatchChatMessageDto.MessageType.QUIT);
                chatMessage.setMessage(senderNickname + " 님이 채팅방을 나갔습니다.");
                messagingTemplate.convertAndSend("/topic/matchchat/" + chatRoomId, chatMessage);
            } else {
                // 더 이상 참가자가 없으면 채팅방 제거
                chatRoomService.removeChatRoom(chatRoomId);
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {   // 예기치 않은 연결 끊김(브라우저 닫기 등)
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        String nickname = (String) headerAccessor.getSessionAttributes().get("nickname");
        String chatRoomId = (String) headerAccessor.getSessionAttributes().get("roomId");
        String userId = (String) headerAccessor.getSessionAttributes().get("userId");

        if (nickname != null && chatRoomId != null && userId != null) {

            Map<String, String> participants = chatRoomService.getParticipants(chatRoomId);
            if (participants != null) {
                participants.remove(userId);

                if (!participants.isEmpty()){
                    // 채팅방 제거
                    chatRoomService.removeChatRoom(chatRoomId);
                }
            }
        }
    }
}