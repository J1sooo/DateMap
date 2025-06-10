package com.est.back;

import com.est.back.blindChat.repository.BlindDateFeedbackRepository;
import com.est.back.chatroom.ChatroomRepository;
import com.est.back.partner.PartnerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class WeeklyResetTask {

    private final BlindDateFeedbackRepository blindDateFeedbackRepository;
    private final ChatroomRepository chatroomRepository;
    private final PartnerRepository partnerRepository;

    // (초 분 시 요일 월 요일)
    @Transactional
    @Scheduled(cron = "0 00 00 * * MON")
    public void resetTable() {
        blindDateFeedbackRepository.deleteAll();
        chatroomRepository.deleteAll();
        partnerRepository.deleteAllExceptFixedIds();
        System.out.println("테이블 데이터 초기화 완료");
    }
}

