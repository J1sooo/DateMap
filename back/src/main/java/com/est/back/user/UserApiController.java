package com.est.back.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.status(500).body(Map.of("success", false, "message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
        }
    }
}
