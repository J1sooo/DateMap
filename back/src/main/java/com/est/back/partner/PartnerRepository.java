package com.est.back.partner;

import com.est.back.partner.domain.Partner;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    @Query("SELECT p FROM Partner p WHERE p.charId = (SELECT c.partnerId FROM Chatroom c WHERE c.id = :chatroomId)")
    Partner findPartnerByChatroomId(@Param("chatroomId") Long chatroomId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM blind_date_character WHERE char_id NOT IN (1, 2, 3, 4)", nativeQuery = true)
    void deleteAllExceptFixedIds();

}
