package com.est.back.partner;

import com.est.back.partner.domain.Partner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    @Query("SELECT p FROM Partner p WHERE p.charId = (SELECT c.partnerId FROM Chatroom c WHERE c.id = :chatroomId)")
    Partner findPartnerByChatroomId(@Param("chatroomId") Long chatroomId);

}
