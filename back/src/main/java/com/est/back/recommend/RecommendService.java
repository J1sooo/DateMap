package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import com.est.back.recommend.dto.RecommendResponseDto;
import com.est.back.s3.ImageUploadService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class RecommendService {

    private final RecommendRepository repository;
    private final ImageUploadService imageUploadService;

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

    public List<RecommendResponseDto> getUserCourses(Long usn) {
        List<Recommend> courses = repository.findAllByUsn(usn).stream()
                .sorted((r1, r2) -> Long.compare(r2.getCourseId(), r1.getCourseId())) // 최신순 정렬
//                .limit(4) // 가장 최근 4개만
                .collect(Collectors.toList());

        return courses.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public List<RecommendResponseDto> getRecentRecommends() {
        return repository.findTop4ByOrderByCourseIdDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Recommend saveRecommend(Recommend recommend) {
        return repository.save(recommend);
    }

    public boolean isOwner(Long courseId, Long usn) {
        return repository.findById(courseId)
                .map(course -> course.getUsn().equals(usn))
                .orElse(false);
    }

    @Transactional
    public void deleteRecommendById(Long id) {
        Recommend recommend = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("recommend not found"));
        imageUploadService.deleteFile(recommend.getImageUrl());
        repository.deleteById(id);
    }

    public void deleteAllRecommends(){
        repository.deleteAll();
    }

    @Transactional
    public RecommendResponseDto updateImage(Long id, MultipartFile image) throws IOException {
        Recommend recommend = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("recommend not found"));
        imageUploadService.deleteFile(recommend.getImageUrl());

        recommend.setImageUrl(imageUploadService.uploadFile(image));
        return RecommendResponseDto.fromRecommend(recommend);
    }
}
