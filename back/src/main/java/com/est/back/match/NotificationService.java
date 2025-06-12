package com.est.back.match;

import com.est.back.match.dto.NotificationMessageDto;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, List<NotificationMessageDto>> userNotifications = new ConcurrentHashMap<>();

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendAndStoreNotification(String recipientUserId, NotificationMessageDto.NotificationType type,
                                         String senderNickname, String message, String link, String chatRoomId) {

        NotificationMessageDto notification = NotificationMessageDto.builder()
                .recipientUserId(recipientUserId)
                .type(type)
                .senderNickname(senderNickname)
                .message(message)
                .link(link)
                .timestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .read(false)
                .chatRoomId(chatRoomId)
                .build();

        userNotifications.computeIfAbsent(recipientUserId, k -> new CopyOnWriteArrayList<>()).add(notification);
        System.out.println("NotificationService (메모리): " + recipientUserId + "에게 새 알림 저장됨: " + message + (chatRoomId != null ? " (채팅방: " + chatRoomId + ")" : ""));


        messagingTemplate.convertAndSendToUser(
                recipientUserId,
                "/queue/notifications",
                notification
        );
        System.out.println("NotificationService (웹소켓): " + recipientUserId + "에게 알림 푸시됨: " + message);

        getUnreadNotificationCount(recipientUserId);
    }

    public void markAllNotificationsAsRead(String userId) {
        List<NotificationMessageDto> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.forEach(n -> n.setRead(true));
            System.out.println("NotificationService (메모리): " + userId + "의 모든 알림 읽음 처리.");
            // 읽음 처리 후 읽지 않은 알림 개수를 푸시
            getUnreadNotificationCount(userId);
        }
    }

    // 특정 채팅방 관련 알림을 읽음 처리
    public void markChatNotificationsAsRead(String userId, String chatRoomId) {
        List<NotificationMessageDto> notifications = userNotifications.get(userId);
        if (notifications != null) {
            int markedCount = 0;
            for (NotificationMessageDto n : notifications) {
                if (n.getType() == NotificationMessageDto.NotificationType.CHAT_MESSAGE &&
                        chatRoomId.equals(n.getChatRoomId()) &&
                        !n.isRead()) {
                    n.setRead(true);
                    markedCount++;
                }
            }
            if (markedCount > 0) {
                System.out.println("NotificationService (메모리): " + userId + "의 채팅방 " + chatRoomId + " 알림 " + markedCount + "개 읽음 처리.");
                getUnreadNotificationCount(userId);
            }
        }
    }

    public int getUnreadNotificationCount(String userId) {
        List<NotificationMessageDto> notifications = userNotifications.getOrDefault(userId, Collections.emptyList());
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();
        System.out.println("NotificationService (메모리): " + userId + "의 읽지 않은 알림 개수: " + unreadCount);

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/unreadCount",
                (int) unreadCount
        );
        return (int) unreadCount;
    }
}