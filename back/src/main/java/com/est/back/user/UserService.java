package com.est.back.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    // 로그인 기능
    @Transactional
    public User login(LoginRequestDto loginRequestDto) {
        // userId 또는 email로 사용자 조회
        Optional<User> userOptional = userRepository.findByUserId(loginRequestDto.getUsername());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginRequestDto.getUsername());
        }

        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        // 비밀번호 일치 여부
        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    @Transactional
    public void join(JoinRequestDto joinRequestDto) {
        // 중복 확인
        if (userRepository.existsByUserId(joinRequestDto.getUserId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByNickName(joinRequestDto.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        if (userRepository.existsByEmail(joinRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if ((joinRequestDto.getPreferArea() == null || joinRequestDto.getPreferArea().isEmpty()) &&
                (joinRequestDto.getPreferAreaDetail() == null || joinRequestDto.getPreferAreaDetail().isEmpty())) {
            throw new IllegalArgumentException("선호 지역은 최소 1개 이상 선택해야 합니다.");
        }

        String encodedPassword = passwordEncoder.encode(joinRequestDto.getPassword());

        User user = User.builder()
                .userId(joinRequestDto.getUserId())
                .password(encodedPassword)
                .nickName(joinRequestDto.getNickName())
                .email(joinRequestDto.getEmail())
                .gender(joinRequestDto.getGender())
                .dateOfBirth(joinRequestDto.getDateOfBirth())
                .preferArea(joinRequestDto.getPreferArea())
                .preferAreaDetail(joinRequestDto.getPreferAreaDetail())
                .build();

        userRepository.save(user);
        log.info("User joined: {}", user.getUserId());
    }

    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    public boolean existsByNickName(String nickName) {
        return userRepository.existsByNickName(nickName);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // 아이디 찾기 (닉네임&이메일)
    public String findUserIdByNickNameAndEmail(String nickName, String email) {
        Optional<User> userOptional = userRepository.findByNickNameAndEmail(nickName, email);
        return userOptional.map(User::getUserId).orElse(null);
    }

    // 비밀번호 재설정 (아이디&이메일)
    public Optional<User> verifyUserForPasswordReset(String userId, String email) {
        // 사용자 조회
        return userRepository.findByUserIdAndEmail(userId, email);
    }

    //
    @Transactional
    public void resetPasswordDirectly(Long usn, String newPassword) {
        User user = userRepository.findByUsn(usn)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새 비밀번호 업데이트
        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("사용자 USN {} 비밀번호 직접 재설정 성공.", usn);
    }
}