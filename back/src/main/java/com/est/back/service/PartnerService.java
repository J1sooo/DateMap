package com.est.back.service;

import com.est.back.domain.Partner;
import com.est.back.repository.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository repository;

    public Partner savePartner(Partner partner) {
        return repository.save(partner);
    }

    public List<Partner> getAllPartners() {
        return repository.findAll();
    }

    public Partner getPartnerById(Long id){
        return repository.findById(id).orElse(null);
    }

    public void deletePartner(Long id) {
        repository.deleteById(id);
    }

}
