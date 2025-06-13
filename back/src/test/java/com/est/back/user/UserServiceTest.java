package com.est.back.user;

import com.est.back.s3.ImageUploadService;
import com.est.back.user.dto.JoinRequestDto;
import com.est.back.user.dto.LoginRequestDto;
import com.est.back.user.dto.UserUpdateRequestDto;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock ImageUploadService imageUploadService;
    @Mock MultipartFile multipartFile;
    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @InjectMocks UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder, imageUploadService);
    }

    @Test
    void 회원가입_성공() {
        JoinRequestDto dto = new JoinRequestDto();
        dto.setUserId("testuser");
        dto.setPassword("abc12345");
        dto.setPasswordCheck("abc12345");
        dto.setNickName("닉네임");
        dto.setEmail("test@example.com");
        dto.setGender(User.Gender.MALE);
        dto.setBirthYear(1999);
        dto.setBirthMonth(1);
        dto.setBirthDay(10);
        dto.setPreferArea("서울");
        dto.setTermsAgreed(true);
        dto.setPrivacyAgreed(true);

        when(userRepository.existsByUserId(any())).thenReturn(false);
        when(userRepository.existsByNickName(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);

        userService.join(dto);

        verify(userRepository).save(any(User.class));
    }

    @Test
    void 로그인_성공() {
        LoginRequestDto dto = new LoginRequestDto();
        dto.setUsername("testuser");
        dto.setPassword("abc12345");

        User user = User.builder()
                .userId("testuser")
                .password(passwordEncoder.encode("abc12345"))
                .nickName("닉네임")
                .email("test@example.com")
                .gender(User.Gender.MALE)
                .dateOfBirth(LocalDate.of(1999,1,1))
                .preferArea("서울")
                .build();

        when(userRepository.findByUserId("testuser")).thenReturn(Optional.of(user));

        User loggedIn = userService.login(dto);

        assertThat(loggedIn.getUserId()).isEqualTo("testuser");
    }

    @Test
    void 회원정보_수정_성공() throws Exception {
        User user = User.builder()
                .usn(1L)
                .userId("testuser")
                .password(passwordEncoder.encode("abc12345"))
                .nickName("닉네임")
                .email("test@example.com")
                .gender(User.Gender.MALE)
                .dateOfBirth(LocalDate.of(1999,1,1))
                .preferArea("서울")
                .build();

        UserUpdateRequestDto dto = new UserUpdateRequestDto();
        dto.setUsn(1L);
        dto.setNickName("새닉네임");
        dto.setEmail("new@email.com");
        dto.setGender(User.Gender.FEMALE);
        dto.setBirthYear(1990);
        dto.setBirthMonth(2);
        dto.setBirthDay(20);
        dto.setPreferArea("부산");

        when(userRepository.findByUsn(1L)).thenReturn(Optional.of(user));
        when(imageUploadService.uploadFile(any())).thenReturn("https://test-bucket.s3.amazonaws.com/newprofile.jpg");

        User updated = userService.updateUser(1L, dto, multipartFile);

        assertThat(updated.getNickName()).isEqualTo("새닉네임");
        assertThat(updated.getEmail()).isEqualTo("new@email.com");
        assertThat(updated.getProfileImg()).contains("newprofile.jpg");
    }

    @Test
    void 회원탈퇴_성공() {
        User user = User.builder().usn(2L).userId("bye").build();
        when(userRepository.findByUsn(2L)).thenReturn(Optional.of(user));
        userService.deleteUser(2L);
        verify(userRepository).delete(user);
    }
}
