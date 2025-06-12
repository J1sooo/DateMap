package com.est.back.mypage;

import com.est.back.chatroom.ChatroomRepository;
import com.est.back.chatroom.domain.Chatroom;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.est.back.blindChat.repository.BlindDateFeedbackRepository;
import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.partner.PartnerRepository;
import com.est.back.partner.domain.Partner;
import com.est.back.mypage.dto.MypagePartnerResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final BlindDateFeedbackRepository feedbackRepository;
    private final PartnerRepository partnerRepository;
    private final ChatroomRepository chatroomRepository;

    public int getFeedbackCount(Long usn){
        int feedbackCount = feedbackRepository.findAllByUsn(usn).size();
        return feedbackCount;
    }

    public List<MypagePartnerResponseDto> getMyPartners(Long usn) {
        List<BlindDateFeedback> feedbackList = feedbackRepository.findAllByUsn(usn).stream()
                .sorted((f1, f2) -> Long.compare(f2.getId(), f1.getId())) // ID 내림차순 (최신순)
                .limit(4) // 최대 4개
                .collect(Collectors.toList());

        return feedbackList.stream().map(feedback -> {

            Long partnerId = feedback.getCharId();

            Partner partner = partnerRepository.findById(partnerId)
                    .orElseThrow(() -> new IllegalArgumentException("char_id에 해당하는 캐릭터 정보가 없습니다."));

            Long chatId = chatroomRepository.findByPartnerId(partnerId)
                    .map(Chatroom::getId)
                    .orElse(null);

            return MypagePartnerResponseDto.builder()
                    .id(feedback.getId())
                    .chatId(chatId)
                    .summary(feedback.getSummary())
                    .feedback(feedback.getFeedback())
                    .score(feedback.getScore())
                    .imageUrl(partner.getImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }

}
