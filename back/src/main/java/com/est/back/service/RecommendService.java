package com.est.back.service;

import com.est.back.domain.Partner;
import com.est.back.domain.Recommend;
import com.est.back.repository.RecommendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendService {

    private final RecommendRepository repository;

    @Autowired
    public RecommendService(RecommendRepository repository) {
        this.repository = repository;
    }

    public Recommend saveRecommend(Recommend recommend) {
        return repository.save(recommend);
    }

    public List<Recommend> getAllRecommends() {
        return repository.findAll();
    }

    public Recommend getRecommendById(Long id){
        return repository.findById(id).orElse(null);
    }

    public void deleteRecommendById(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllRecommends() {
        repository.deleteAll();
    }

}
