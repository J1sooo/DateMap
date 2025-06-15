package com.est.back.blindChat.repository;

import com.est.back.blindChat.domain.BlindDateFeedback;
import com.est.back.partner.domain.Partner;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BlindDateFeedbackRepository extends JpaRepository<BlindDateFeedback, Long> {

    Optional<BlindDateFeedback> findByCharId(Long charId);

    List<BlindDateFeedback> findAllByUsn(Long usn);

    long countByUsn(Long usn);

    @Query("SELECT p FROM Partner p WHERE p.charId = (SELECT c.partnerId FROM Chatroom c WHERE c.id = :chatroomId)")
    Partner findPartnerByChatroomId(@Param("chatroomId") Long chatroomId);
}
