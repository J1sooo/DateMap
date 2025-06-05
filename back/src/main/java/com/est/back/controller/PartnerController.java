package com.est.back.controller;

import com.est.back.domain.Partner;
import com.est.back.service.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/partner")
public class PartnerController {

    private final PartnerService partnerService;

    @Autowired
    public PartnerController(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    // POST /api/partner
    // ChatroomController에서 처리함.
//    @PostMapping
//    public Partner createPartner(@ModelAttribute Partner partner) {
//        partner.setCreatedAt(LocalDateTime.now());
//        return partnerService.savePartner(partner);
//    }

    // GET /api/partner/{id}
    @GetMapping("/{id}")
    public Partner getPartner(@PathVariable Long id) {
        return partnerService.getPartnerById(id);
    }

    // DELETE /api/partner/{id}
    @DeleteMapping("/{id}")
    public void deletePartner(@PathVariable Long id) {

        partnerService.deletePartner(id);
    }
}
