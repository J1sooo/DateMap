package com.est.back.mypage;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.blindChat.repository.BlindDateFeedbackRepository;
import com.est.back.chatroom.ChatroomRepository;
import com.est.back.chatroom.domain.Chatroom;
import com.est.back.commenMethod.CommonMethod;
import com.est.back.mypage.dto.MypagePartnerResponseDto;
import com.est.back.partner.PartnerRepository;
import com.est.back.partner.domain.Partner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MypageService 단위 테스트")
class MypageServiceTest {

    @Mock
    private BlindDateFeedbackRepository feedbackRepository;

    @Mock
    private PartnerRepository partnerRepository;

    @Mock
    private ChatroomRepository chatroomRepository;

    @InjectMocks
    private MypageService mypageService;

    private BlindDateFeedback feedback;
    private Partner partner;
    private Chatroom chatroom;

    @BeforeEach
    void setUp() {
        Long usn = 1L;
        Long charId = 10L;
        Long chatId = 100L;

        feedback = CommonMethod.createMockFeedback(1L, usn, charId);
        partner = CommonMethod.createMockPartner();
        chatroom = CommonMethod.createMockChatroom(chatId, usn, charId);
    }

    @Test
    @DisplayName("given 유저가 피드백을 작성한 경우, when getFeedbackCount 호출하면 then 피드백 수 반환")
    void givenFeedbacks_whenGetFeedbackCount_thenReturnsSize() {
        // given
        when(feedbackRepository.findAllByUsn(1L)).thenReturn(List.of(feedback));

        // when
        int count = mypageService.getFeedbackCount(1L);

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("given 유저의 피드백이 존재할 때, when getMyPartners 호출하면 then DTO 리스트를 반환한다")
    void givenFeedbacks_whenGetMyPartners_thenReturnsDtoList() {
        // given
        when(feedbackRepository.findAllByUsn(1L)).thenReturn(List.of(feedback));
        when(partnerRepository.findById(10L)).thenReturn(Optional.of(partner));
        when(chatroomRepository.findByPartnerId(10L)).thenReturn(Optional.of(chatroom));

        // when
        List<MypagePartnerResponseDto> result = mypageService.getMyPartners(1L);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(feedback.getId());
        assertThat(result.get(0).getChatId()).isEqualTo(chatroom.getId());
        assertThat(result.get(0).getImageUrl()).isEqualTo(partner.getImageUrl());
        assertThat(result.get(0).getFeedback()).isEqualTo(feedback.getFeedback());
    }
}
