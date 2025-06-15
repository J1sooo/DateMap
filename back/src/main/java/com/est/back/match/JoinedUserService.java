package com.est.back.match;

import com.est.back.user.User;
import com.est.back.user.UserRepository;
import com.est.back.user.dto.UserInfoResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JoinedUserService {

    private final UserRepository userRepository;
    private final HttpServletRequest request;

    public List<UserInfoResponseDto> getMatchableOnlineUsers(Long currentUsn, User.Gender currentUserGender) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다. 세션이 존재하지 않습니다.");
        }

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        if (loggedInUser == null) {
            throw new IllegalStateException("인증되지 않은 사용자입니다. 세션에 사용자 정보가 없습니다.");
        }

        // 모든 사용자 DB에서 조회
        List<User> allUsersInDb = userRepository.findAll();

        return allUsersInDb.stream()
                .filter(user -> user.getUsn() != null && !user.getUsn().equals(currentUsn)) // 자기 자신 제외
                .filter(user -> {
                    // 이성 매칭을 위한 성별 필터링
                    return user.getGender() != null && user.getGender() != currentUserGender;
                })
                .map(UserInfoResponseDto::from)
                .collect(Collectors.toList());
    }
}