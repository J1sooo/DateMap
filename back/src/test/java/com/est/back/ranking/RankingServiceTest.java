package com.est.back.ranking;

import com.est.back.ranking.dto.RankingDto;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("RankingService 통합 테스트")
class RankingServiceTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private RankingService rankingService;

    // 테스트 실행 전에 전체 테이블 초기화
    @BeforeEach
    void clearTables() {
        em.createNativeQuery("DELETE FROM blind_date_feedback").executeUpdate();
        em.createNativeQuery("DELETE FROM blind_date_character").executeUpdate();
        em.createNativeQuery("DELETE FROM USER").executeUpdate();
    }

    @Test
    @DisplayName("given 피드백 데이터가 있을 때, when getRanking 호출하면 then 최고 점수 기준으로 TOP10 반환")
    void givenFeedbackData_whenGetRanking_thenReturnsTop10ByScore() {
        // given
        insertUser(1L, "user1", "pass", "수지", "수지@example.com", "FEMALE", "수지.jpg");
        insertUser(2L, "user2", "pass", "진구", "진구@example.com", "MALE", "진구.jpg");

        insertPartner(1L);
        insertPartner(2L);
        insertPartner(3L);

        insertFeedback(1L, 1L, LocalDateTime.now(), "summary", "feedback", 95);
        insertFeedback(1L, 2L, LocalDateTime.now(), "summary", "feedback", 98);
        insertFeedback(2L, 3L, LocalDateTime.now(), "summary", "feedback", 90);

        // when
        List<RankingDto> ranking = rankingService.getRanking();

        // then
        assertThat(ranking).isNotEmpty();
        assertThat(ranking.get(0).getUsn()).isEqualTo(1L);
        assertThat(ranking.get(0).getScore()).isEqualTo(98);
    }

    @Test
    @DisplayName("given 피드백 여러 건이 있을 때, when getRankingByCount 호출하면 then 참여 횟수 기준 TOP10 반환")
    void givenMultipleFeedbacks_whenGetRankingByCount_thenReturnsTop10ByCount() {
        // given
        insertUser(1L, "user1", "pass", "수지", "수지@example.com", "FEMALE", "수지.jpg");
        insertUser(2L, "user2", "pass", "진구", "진구@example.com", "MALE", "진구.jpg");

        insertPartner(1L);
        insertPartner(2L);
        insertPartner(3L);
        insertPartner(4L);

        insertFeedback(1L, 1L, LocalDateTime.now(), "summary", "feedback", 95);
        insertFeedback(1L, 2L, LocalDateTime.now(), "summary", "feedback", 90);
        insertFeedback(1L, 3L, LocalDateTime.now(), "summary", "feedback", 80);
        insertFeedback(2L, 4L, LocalDateTime.now(), "summary", "feedback", 99);

        // when
        List<RankingDto> result = rankingService.getRankingByCount();

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getUsn()).isEqualTo(1L);
        assertThat(result.get(0).getCount()).isEqualTo(3);
    }

    // --- 유저 삽입 메서드 ---
    private void insertUser(Long usn, String userId, String password, String nickName,
                            String email, String gender, String profileImg) {

        em.createNativeQuery("DELETE FROM USER WHERE usn = ?")
                .setParameter(1, usn)
                .executeUpdate();

        em.createNativeQuery("""
            INSERT INTO USER (
                usn, user_id, password, nickName, email, gender,
                date_of_birth, prefer_area, prefer_area_detail,
                join_date, profile_img
            )
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, now(), ?)
        """)
                .setParameter(1, usn)
                .setParameter(2, userId)
                .setParameter(3, password)
                .setParameter(4, nickName)
                .setParameter(5, email)
                .setParameter(6, gender)
                .setParameter(7, LocalDate.of(2000, 1, 1))
                .setParameter(8, "서울")
                .setParameter(9, "강남")
                .setParameter(10, profileImg)
                .executeUpdate();
    }

    // --- 피드백 삽입 메서드 ---
    private void insertFeedback(Long usn, Long charId, LocalDateTime createdAt,
                                String summary, String feedback, int score) {
        em.createNativeQuery("""
            INSERT INTO blind_date_feedback (
                usn, char_id, created_at, summary, feedback, score
            )
            VALUES (?, ?, ?, ?, ?, ?)
        """)
                .setParameter(1, usn)
                .setParameter(2, charId)
                .setParameter(3, createdAt)
                .setParameter(4, summary)
                .setParameter(5, feedback)
                .setParameter(6, score)
                .executeUpdate();
    }

    // --- 캐릭터 삽입 메서드 ---
    private void insertPartner(Long charId) {
        em.createNativeQuery("DELETE FROM blind_date_character WHERE char_id = ?")
                .setParameter(1, charId)
                .executeUpdate();

        em.createNativeQuery("""
            INSERT INTO blind_date_character (
                char_id, gender, age_group, personal_type, hobby, image_url, created_at
            )
            VALUES (?, 'FEMALE', '20대', 'ENFP', '영화보기', 'partner.jpg', now())
        """)
                .setParameter(1, charId)
                .executeUpdate();
    }
}
