package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {
    List<LessonProgress> findByUserIdAndIsCompletedTrue(Long userId);
}
