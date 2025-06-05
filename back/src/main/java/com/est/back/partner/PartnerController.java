package com.est.back.partner;

import com.est.back.partner.domain.Partner;
import com.est.back.partner.dto.PartnerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public PartnerResponseDto getPartner(@PathVariable Long id) {
        return partnerService.getPartnerById(id);
    }

    // DELETE /api/partner/{id}
    @DeleteMapping("/{id}")
    public void deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        // todo delete with chatroom
    }

}
