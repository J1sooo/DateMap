package com.est.back.match;

import com.est.back.user.UserService;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class MatchChatRoomService {

    private final Map<String, Map<String, String>> activeChatRooms = new ConcurrentHashMap<>();
    private final NotificationService notificationService;
    private final UserService userService;

    public MatchChatRoomService(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    public void addChatRoom(String chatRoomId, Map<String, String> participants) {
        activeChatRooms.put(chatRoomId, participants);
    }

    public Map<String, String> getParticipants(String chatRoomId) {
        return activeChatRooms.get(chatRoomId);
    }

    public void removeChatRoom(String chatRoomId) {
        activeChatRooms.remove(chatRoomId);
    }
}