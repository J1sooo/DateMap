package com.est.back.mypage;

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

    public List<MypagePartnerResponseDto> getMyPartners(Long usn) {
        List<BlindDateFeedback> feedbackList = feedbackRepository.findAllByUsn(usn);

        return feedbackList.stream().map(feedback -> {
            Partner partner = partnerRepository.findById(feedback.getCharId())
                    .orElseThrow(() -> new IllegalArgumentException("char_id에 해당하는 캐릭터 정보가 없습니다."));

            return MypagePartnerResponseDto.builder()
                    .id(feedback.getId())
                    .summary(feedback.getSummary())
                    .feedback(feedback.getFeedback())
                    .score(feedback.getScore())
                    .imageUrl(partner.getImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }
}
