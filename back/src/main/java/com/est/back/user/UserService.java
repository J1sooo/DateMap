package com.est.back.user;

import com.est.back.s3.ImageUploadService;
import com.est.back.user.dto.JoinRequestDto;
import com.est.back.user.dto.LoginRequestDto;
import com.est.back.user.dto.UserUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    @Transactional
    public User login(LoginRequestDto loginRequestDto) {
        Optional<User> userOptional = userRepository.findByUserId(loginRequestDto.getUsername());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginRequestDto.getUsername());
        }

        User user = userOptional.orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        return user;
    }

    @Transactional
    public void join(JoinRequestDto joinRequestDto) {
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

        validatePassword(joinRequestDto.getPassword());

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
                .profileImg(String.format("https://%s.s3.ap-northeast-2.amazonaws.com/default_profile.png", bucketName))
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

    public String findUserIdByNickNameAndEmail(String nickName, String email) {
        Optional<User> userOptional = userRepository.findByNickNameAndEmail(nickName, email);
        return userOptional.map(User::getUserId).orElse(null);
    }

    public Optional<User> verifyUserForPasswordReset(String userId, String email) {
        return userRepository.findByUserIdAndEmail(userId, email);
    }

    @Transactional
    public void resetPasswordDirectly(Long usn, String newPassword) {
        User user = userRepository.findByUsn(usn)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        validatePassword(newPassword);

        user.updatePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        log.info("사용자 USN {} 비밀번호 직접 재설정 성공.", usn);
    }

    @Transactional
    public User updateUser(Long usn, UserUpdateRequestDto updateRequestDto,
                           MultipartFile profileImageFile) {
        User user = userRepository.findByUsn(usn)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 닉네임 중복 체크
        if (!user.getNickName().equals(updateRequestDto.getNickName()) && userRepository.existsByNickName(updateRequestDto.getNickName())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 이메일 중복 체크
        if (!user.getEmail().equals(updateRequestDto.getEmail()) && userRepository.existsByEmail(updateRequestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 변경
        if (updateRequestDto.getNewPassword() != null && !updateRequestDto.getNewPassword().isEmpty()) {
            if (updateRequestDto.getCurrentPassword() == null || updateRequestDto.getCurrentPassword().isEmpty()) {
                throw new IllegalArgumentException("새 비밀번호를 입력하면 현재 비밀번호도 입력해야 합니다.");
            }
            if (!passwordEncoder.matches(updateRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            validatePassword(updateRequestDto.getNewPassword());

            user.updatePassword(passwordEncoder.encode(updateRequestDto.getNewPassword()));
            log.info("사용자 USN {} 비밀번호 변경 성공.", usn);
        } else {
            if ((updateRequestDto.getCurrentPassword() != null && !updateRequestDto.getCurrentPassword().isEmpty()) ||
                    (updateRequestDto.getNewPasswordCheck() != null && !updateRequestDto.getNewPasswordCheck().isEmpty())) {
                throw new IllegalArgumentException("비밀번호를 변경하지 않으려면, 모든 비밀번호 관련 필드를 비워두세요.");
            }
        }

        // 선호 지역
        if ((updateRequestDto.getPreferArea() == null || updateRequestDto.getPreferArea().isEmpty()) &&
                (updateRequestDto.getPreferAreaDetail() == null || updateRequestDto.getPreferAreaDetail().isEmpty())) {
            throw new IllegalArgumentException("선호 지역은 최소 1개 이상 선택해야 합니다.");
        }

        // 프로필 이미지
        try {
            String newImageUrl = imageUploadService.uploadFile(profileImageFile);
            user.updateProfileImg(newImageUrl);
            log.info("사용자 USN {} 새 프로필 이미지 업로드됨: {}", usn, newImageUrl);
        } catch (IOException e) {
            log.error("프로필 이미지 업로드 중 오류 발생", e);
            throw new IllegalStateException("프로필 이미지 업로드에 실패했습니다. 다시 시도해주세요.");
        }


        user.updateUserInfo(
                updateRequestDto.getNickName(),
                updateRequestDto.getEmail(),
                updateRequestDto.getGender(),
                updateRequestDto.getDateOfBirth(),
                updateRequestDto.getPreferArea(),
                updateRequestDto.getPreferAreaDetail()
        );

        userRepository.save(user);
        log.info("사용자 USN {} 회원 정보 수정 성공.", usn);
        return user;
    }

    public User getUserInfo(Long usn) {
        return userRepository.findByUsn(usn)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호는 필수 입력 값입니다.");
        }
        if (password.length() < 8 || password.length() > 20) {
            throw new IllegalArgumentException("비밀번호는 8~20자 이내로 입력해주세요.");
        }
        Pattern pattern = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]+$");
        Matcher matcher = pattern.matcher(password);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("비밀번호는 영문과 숫자를 반드시 포함해야 합니다.");
        }
    }

    @Transactional
    public void deleteUser(Long usn) {
        User user = userRepository.findByUsn(usn)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userRepository.delete(user);
        log.info("사용자 USN {} 회원 탈퇴 성공.", usn);
    }
}