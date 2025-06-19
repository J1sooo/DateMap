package com.est.back.recommend;

import com.est.back.recommend.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
    List<Recommend> findAllByUsn(Long usn);
    List<Recommend> findTop4ByOrderByCourseIdDesc();
    List<Recommend> findByAreaContainingOrAreaContainingOrderBySavedAtDesc(String area1, String area2);

    List<Recommend> findByAreaContainingOrderBySavedAtDesc(String area);

    List<Recommend> findTop10ByOrderBySavedAtDesc();

}
