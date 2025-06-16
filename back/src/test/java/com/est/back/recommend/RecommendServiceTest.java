package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import com.est.back.recommend.dto.RecommendResponseDto;
import com.est.back.s3.ImageUploadService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("RecommendService 통합 테스트")
class RecommendServiceTest {

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private RecommendRepository recommendRepository;

    @MockBean
    private ImageUploadService imageUploadService;

    private Recommend recommend1;
    private Recommend recommend2;

    @BeforeEach
    void setUp() {
        recommend1 = Recommend.builder()
                .usn(1L)
                .imageUrl("image1.jpg")
                .title("코스1")
                .content1("맛집")
                .content2("카페")
                .content3("산책")
                .content4("전시회")
                .area("서울")
                .savedAt(LocalDateTime.now().minusDays(1))
                .build();

        recommend2 = Recommend.builder()
                .usn(1L)
                .imageUrl("image2.jpg")
                .title("코스2")
                .content1("영화관")
                .content2("디저트카페")
                .content3("쇼핑")
                .content4("야경")
                .area("강남")
                .savedAt(LocalDateTime.now())
                .build();

        recommendRepository.saveAll(List.of(recommend1, recommend2));
    }

    @Test
    @DisplayName("given 추천을 저장할 때, when saveRecommend 호출하면 then courseId가 부여된다")
    void givenRecommend_whenSave_thenIdGenerated() {
        Recommend saved = recommendService.saveRecommend(recommend1);
        assertThat(saved.getCourseId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo(recommend1.getTitle());
    }

    @Test
    @DisplayName("given 추천이 존재할 때, when getAllRecommends 호출하면 then 전체 목록이 반환된다")
    void getAllRecommends_returnsAll() {
        List<RecommendResponseDto> result = recommendService.getAllRecommends();
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("given 저장된 ID가 있을 때, when getRecommendById 호출하면 then 해당 DTO가 반환된다")
    void getById_returnsDto() {
        Recommend saved = recommendRepository.save(recommend1);
        RecommendResponseDto dto = recommendService.getRecommendById(saved.getCourseId());
        assertThat(dto).isNotNull();
        assertThat(dto.getTitle()).isEqualTo(recommend1.getTitle());
    }

    @Test
    @DisplayName("given 특정 usn의 추천이 있을 때, when getRecommendsByUsn 호출하면 then 해당 유저의 목록 반환")
    void getByUsn_returnsUserRecommends() {
        List<RecommendResponseDto> result = recommendService.getRecommendsByUsn(1L);
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("given 추천이 있을 때, when getRecentRecommends 호출하면 then 최근 4개까지 반환된다")
    void getRecentRecommends_returnsTop4() {
        List<RecommendResponseDto> result = recommendService.getRecentRecommends();
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("given 추천이 저장되어 있을 때, when isOwner 호출하면 then 작성자 여부를 반환한다")
    void isOwner_worksCorrectly() {
        Long courseId = recommend1.getCourseId();
        boolean result = recommendService.isOwner(courseId, 1L);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("given 추천이 저장되어 있을 때, when deleteRecommendById 호출하면 then DB에서 제거된다")
    void deleteRecommendById_removesEntity() {
        Long id = recommend1.getCourseId();
        Mockito.doNothing().when(imageUploadService).deleteFile(recommend1.getImageUrl());

        recommendService.deleteRecommendById(id);

        assertThat(recommendRepository.findById(id)).isNotPresent();
    }
}
