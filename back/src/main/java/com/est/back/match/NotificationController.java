package com.est.back.match;

import com.est.back.match.dto.NotificationMessageDto;
import com.est.back.user.User;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 읽지 않은 알림 개수 조회
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Long> getUnreadNotificationCount(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).build();
        }
        long count = notificationService.getUnreadNotificationCount(loggedInUser.getUserId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("")
    @ResponseBody
    public ResponseEntity<List<NotificationMessageDto>> getAllNotifications(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).build();
        }

        List<NotificationMessageDto> notifications = notificationService.getAllNotifications(loggedInUser.getUserId());
        return ResponseEntity.ok(notifications);
    }


    // 특정 알림을 읽음 처리
    @PostMapping("/mark-all-read")
    @ResponseBody
    public ResponseEntity<Void> markAllNotificationsAsRead(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).build();
        }
        notificationService.markAllNotificationsAsRead(loggedInUser.getUserId());
        return ResponseEntity.ok().build();
    }

}