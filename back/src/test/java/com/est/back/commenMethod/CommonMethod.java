package com.est.back.commenMethod;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.chatroom.domain.Chatroom;
import com.est.back.partner.domain.Partner;
import com.est.back.user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CommonMethod {

    public static User createMockUser(Long usn) {
        return User.builder()
                .usn(usn)
                .userId("testuser" + usn)
                .password("testpass")
                .nickName("Tester")
                .email("test" + usn + "@example.com")
                .gender(User.Gender.MALE)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .preferArea("서울")
                .preferAreaDetail("강남")
                .joinDate(LocalDateTime.now())
                .profileImg("profile.jpg")
                .build();
    }

    public static Partner createMockPartner() {
        return Partner.builder()
                .gender("FEMALE")
                .ageGroup("20대")
                .personalType("ENFP")
                .hobby("영화보기")
                .imageUrl("partner" + ".jpg")
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Chatroom createMockChatroom(Long id, Long usn, Long partnerId) {
        return Chatroom.builder()
                .id(id)
                .usn(usn)
                .partnerId(partnerId)
                .build();
    }

    public static BlindDateFeedback createMockFeedback(Long id, Long usn, Long charId) {
        return BlindDateFeedback.builder()
                .id(id)
                .usn(usn)
                .charId(charId)
                .summary("짧은 소개")
                .feedback("매너 좋았어요")
                .score(4)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
