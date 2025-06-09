package com.est.back.user;

import com.est.back.user.dto.JoinRequestDto;
import com.est.back.user.dto.UserUpdateRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    @GetMapping("/checkUserId")
    public Map<String, Boolean> checkUserId(@RequestParam String userId) {
        boolean exists = userService.existsByUserId(userId);
        return Map.of("exists", exists);
    }

    @GetMapping("/checkNickName")
    public Map<String, Boolean> checkNickName(@RequestParam String nickName) {
        boolean exists = userService.existsByNickName(nickName);
        return Map.of("exists", exists);
    }

    @GetMapping("/checkEmail")
    public Map<String, Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);
        return Map.of("exists", exists);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> joinUserApi(@RequestBody JoinRequestDto joinRequestDto) {
        try {
            userService.join(joinRequestDto);
            return ResponseEntity.ok(Map.of("success", true, "message", "회원가입이 완료되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("회원가입 API 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/findId")
    public ResponseEntity<Map<String, Object>> findId(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");

        log.info("아이디 찾기 요청 - 이름: {}, 이메일: {}", name, email);

        try {
            String foundUserId = userService.findUserIdByNickNameAndEmail(name, email);

            if (foundUserId != null) {
                log.info("아이디 찾기 성공: {}", foundUserId);
                return ResponseEntity.ok(Map.of("success", true, "userId", foundUserId));
            } else {
                log.warn("아이디 찾기 실패: 정보 불일치 (이름: {}, 이메일: {})", name, email);
                return ResponseEntity.ok(Map.of("success", false, "message", "입력하신 정보와 일치하는 아이디가 없습니다."));
            }
        } catch (Exception e) {
            log.error("아이디 찾기 중 서버 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/verifyPassword")
    public ResponseEntity<Map<String, Object>> verifyUserForPasswordReset(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String email = request.get("email");

        log.info("비밀번호 재설정 본인 확인 요청 - 아이디: {}, 이메일: {}", username, email);

        try {
            Optional<User> userOptional = userService.verifyUserForPasswordReset(username, email);

            if (userOptional.isPresent()) {
                log.info("비밀번호 재설정 본인 확인 성공: 사용자 USN: {}", userOptional.get().getUsn());
                return ResponseEntity.ok(Map.of("success", true, "usn", userOptional.get().getUsn()));
            } else {
                log.warn("비밀번호 재설정 본인 확인 실패: 정보 불일치 (아이디: {}, 이메일: {})", username, email);
                return ResponseEntity.ok(Map.of("success", false, "message", "아이디 또는 이메일 정보가 일치하지 않습니다."));
            }
        } catch (Exception e) {
            log.error("비밀번호 재설정 본인 확인 중 서버 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, Object>> resetPasswordDirectly(@RequestBody Map<String, String> request) {
        Long usn = Long.parseLong(request.get("usn"));
        String newPassword = request.get("newPassword");

        log.info("비밀번호 직접 재설정 요청 - 사용자 USN: {}", usn);

        try {
            userService.resetPasswordDirectly(usn, newPassword);
            log.info("비밀번호 직접 재설정 성공: 사용자 USN: {}", usn);
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            log.warn("비밀번호 직접 재설정 실패: {}", e.getMessage());
            return ResponseEntity.ok(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("비밀번호 직접 재설정 중 서버 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        try {
            User user = userService.getUserInfo(loggedInUser.getUsn());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "user", user
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("회원 정보 조회 API 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }

    @PatchMapping(value = "/profile", consumes = {"multipart/form-data"})
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @Valid @ModelAttribute UserUpdateRequestDto updateRequestDto,
            BindingResult bindingResult,
            HttpSession session,
            @RequestParam(value = "profileImageFile", required = false) MultipartFile profileImageFile
    ) {

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", errorMessage));
        }

        try {
            User updatedUser = userService.updateUser(
                    loggedInUser.getUsn(),
                    updateRequestDto,
                    profileImageFile
            );
            session.setAttribute("loggedInUser", updatedUser);
            return ResponseEntity.ok(Map.of("success", true, "message", "회원 정보가 수정되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(500).body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("회원 정보 수정 API 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다."));
        }
    }
    @DeleteMapping("/{usn}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("usn") Long usn, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return ResponseEntity.status(401).body(Map.of("success", false, "message", "로그인이 필요합니다."));
        }
        if (!loggedInUser.getUsn().equals(usn)) {
            return ResponseEntity.status(403).body(Map.of("success", false, "message", "권한이 없습니다."));
        }

        try {
            userService.deleteUser(usn);
            session.invalidate();
            return ResponseEntity.ok(Map.of("success", true, "message", "회원 탈퇴가 성공적으로 처리되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            log.error("회원 탈퇴 API 오류 발생", e);
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "회원 탈퇴 중 서버 오류가 발생했습니다."));
        }
    }
}