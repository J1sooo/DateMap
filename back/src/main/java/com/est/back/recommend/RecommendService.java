package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import com.est.back.recommend.dto.RecommendResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendService {

    private final RecommendRepository repository;

    @Autowired
    public RecommendService(RecommendRepository repository) {
        this.repository = repository;
    }

    private RecommendResponseDto toDto(Recommend recommend) {
        return RecommendResponseDto.builder()
                .courseId(recommend.getCourseId())
                .imageUrl(recommend.getImageUrl())
                .title(recommend.getTitle())
                .content1(recommend.getContent1())
                .content2(recommend.getContent2())
                .content3(recommend.getContent3())
                .content4(recommend.getContent4())
                .area(recommend.getArea())
                .build();
    }

    public Recommend saveRecommend(Recommend recommend) {
        return repository.save(recommend);
    }

    public List<RecommendResponseDto> getAllRecommends() {
        return repository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RecommendResponseDto getRecommendById(Long id){
        return repository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    public List<RecommendResponseDto> getRecommendsByUsn(Long usn) {
        return repository.findAllByUsn(usn).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void deleteRecommendById(Long id) {
        repository.deleteById(id);
    }

    public void deleteAllRecommends(){
        repository.deleteAll();
    }

}
