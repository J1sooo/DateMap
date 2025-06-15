package com.est.back.partner;

import com.est.back.partner.domain.Partner;
import com.est.back.partner.dto.PartnerResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository repository;

    private PartnerResponseDto toDto(Partner partner) {
        return PartnerResponseDto.builder()
                .imageUrl(partner.getImageUrl())
                .gender(partner.getGender())
                .ageGroup(partner.getAgeGroup())
                .personalType(partner.getPersonalType())
                .hobby(partner.getHobby())
                .build();
    }

    public Partner savePartner(Partner partner) {
        return repository.save(partner);
    }

    public List<PartnerResponseDto> getAllPartners() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PartnerResponseDto getPartnerById(Long id){
        return repository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public void deletePartner(Long id) {
        repository.deleteById(id);
    }

}
