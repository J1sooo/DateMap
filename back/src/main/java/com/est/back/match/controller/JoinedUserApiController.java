package com.est.back.match.controller;

import com.est.back.match.service.JoinedUserService;
import com.est.back.user.User;
import com.est.back.user.dto.UserInfoResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class JoinedUserApiController {

    private final JoinedUserService joinedUserService;
    private final HttpServletRequest request;

    @GetMapping("/matchableUsers")
    public ResponseEntity<List<UserInfoResponseDto>> getMatchableUsers() {
        HttpSession session = request.getSession(false);

        if (session == null) {
            log.warn("GET /api/matchableUsers: Session is null. Unauthorized access attempt.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = (User) session.getAttribute("loggedInUser"); // 세션에서 User 객체 가져옴

        if (currentUser == null) {
            log.warn("GET /api/matchableUsers: LoggedInUser attribute not found in session. Unauthorized access attempt.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 사용자의 usn과 성별 사용
        Long currentUsn = currentUser.getUsn();
        User.Gender currentUserGender = currentUser.getGender();

        try {
            List<UserInfoResponseDto> matchableUsers = joinedUserService.getMatchableOnlineUsers(currentUsn, currentUserGender);
            return ResponseEntity.ok(matchableUsers);
        } catch (Exception e) {
            log.error("매칭 가능한 사용자 목록을 불러오는 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}