package com.est.back.match;

import com.est.back.match.dto.MatchChatMessageDto;
import com.est.back.match.dto.NotificationMessageDto;
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

        System.out.println("채팅 메시지 수신 - 방: " + chatRoomId + ", 발신자: " + senderNickname + ", 내용: " + chatMessage.getMessage());

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
        System.out.println(nickname + " 님이 채팅방 " + chatRoomId + " 에 입장했습니다.");

        notificationService.markChatNotificationsAsRead(userId, chatRoomId);
    }

    @MessageMapping("/chat.quitRoom")
    public void quitRoom(@Payload MatchChatMessageDto chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        String senderNickname = chatMessage.getSenderNickname();
        String chatRoomId = chatMessage.getChatRoomId();
        String senderUserId = (String) headerAccessor.getSessionAttributes().get("userId");

        System.out.println("웹소켓 메시지: " + senderNickname + " 님이 채팅방 " + chatRoomId + "을 떠남.");

        Map<String, String> participants = chatRoomService.getParticipants(chatRoomId);
        if (participants != null) {
            String userIdToRemove = null;
            for (Map.Entry<String, String> entry : participants.entrySet()) {
                if (entry.getValue().equals(senderNickname) && entry.getKey().equals(senderUserId)) {
                    userIdToRemove = entry.getKey();
                    break;
                }
            }
            if (userIdToRemove != null) {
                participants.remove(userIdToRemove); //나간 사용자 제거

                if (!participants.isEmpty()) {
                    chatMessage.setSenderNickname("System"); // 시스템 메시지로 변경
                    chatMessage.setType(MatchChatMessageDto.MessageType.QUIT);
                    chatMessage.setMessage(senderNickname + " 님이 채팅방을 나갔습니다.");
                    messagingTemplate.convertAndSend("/topic/matchchat/" + chatRoomId, chatMessage);
                } else {
                    chatRoomService.removeChatRoom(chatRoomId);
                }
            }
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
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