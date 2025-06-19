package com.est.back.partner;

import com.est.back.commenMethod.CommonMethod;
import com.est.back.partner.domain.Partner;
import com.est.back.partner.dto.PartnerResponseDto;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DisplayName("PartnerService CRUD 통합 테스트")
class PartnerServiceTest {

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private PartnerRepository partnerRepository;

    @Test
    @DisplayName("given 파트너 정보가 있을 때, when savePartner 호출하면 then DB에 저장된다")
    void givenPartner_whenSavePartner_thenSavesToDB() {
        // given
        Partner partner = CommonMethod.createMockPartner();

        // when
        Partner saved = partnerService.savePartner(partner);

        // then
        assertThat(saved.getCharId()).isNotNull();
        assertThat(saved.getGender()).isEqualTo(partner.getGender());
        assertThat(saved.getImageUrl()).isEqualTo(partner.getImageUrl());
    }

    @Test
    @DisplayName("given 여러 파트너가 저장되어 있을 때, when getAllPartners 호출하면 then 모든 파트너를 반환한다")
    void givenMultiplePartners_whenGetAll_thenReturnsAll() {
        // given
        partnerService.savePartner(CommonMethod.createMockPartner());
        partnerService.savePartner(CommonMethod.createMockPartner());

        // when
        List<PartnerResponseDto> result = partnerService.getAllPartners();

        // then
        assertThat(result).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    @DisplayName("given 파트너가 저장되어 있을 때, when getPartnerById 호출하면 then Dto로 반환된다")
    void givenSavedPartner_whenFindById_thenReturnsDto() {
        // given
        Partner saved = partnerService.savePartner(CommonMethod.createMockPartner());
        Long id = saved.getCharId();

        // when
        PartnerResponseDto result = partnerService.getPartnerById(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getImageUrl()).isEqualTo(saved.getImageUrl());
    }

    @Test
    @DisplayName("given 저장된 파트너가 있을 때, when deletePartner 호출하면 then DB에서 제거된다")
    void givenSavedPartner_whenDelete_thenNotFoundInDB() {
        // given
        Partner saved = partnerService.savePartner(CommonMethod.createMockPartner());
        Long id = saved.getCharId();

        // when
        partnerService.deletePartner(id);

        // then
        boolean exists = partnerRepository.findById(id).isPresent();
        assertThat(exists).isFalse();
    }
}
