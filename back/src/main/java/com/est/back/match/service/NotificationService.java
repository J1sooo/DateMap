package com.est.back.match.service;

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

        messagingTemplate.convertAndSendToUser(
                recipientUserId,
                "/queue/notifications",
                notification
        );

        getUnreadNotificationCount(recipientUserId);
    }

    public void markAllNotificationsAsRead(String userId) {
        List<NotificationMessageDto> notifications = userNotifications.get(userId);
        if (notifications != null) {
            // 삭제 방식: 읽은 알림은 리스트에서 제거
            notifications.removeIf(NotificationMessageDto::isRead); // 기존 읽은 것 제거
            notifications.forEach(n -> n.setRead(true));             // 새로 읽은 것으로 표시
            notifications.removeIf(NotificationMessageDto::isRead); // 다시 제거

            System.out.println("📭 읽은 알림 삭제됨 - 남은 개수: " + notifications.size());
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
                getUnreadNotificationCount(userId);
            }
        }
    }

    public int getUnreadNotificationCount(String userId) {
        List<NotificationMessageDto> notifications = userNotifications.getOrDefault(userId, Collections.emptyList());
        long unreadCount = notifications.stream().filter(n -> !n.isRead()).count();

        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/unreadCount",
                (int) unreadCount
        );
        return (int) unreadCount;
    }

    public List<NotificationMessageDto> getAllNotifications(String userId) {
        return userNotifications.getOrDefault(userId, Collections.emptyList());
    }

}