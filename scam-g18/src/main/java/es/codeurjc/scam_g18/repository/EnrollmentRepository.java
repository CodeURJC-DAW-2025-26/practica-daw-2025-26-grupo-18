package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserId(Long userId);

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    boolean existsByUserIdAndCourseIdAndExpiresAtAfter(Long userId, Long courseId, LocalDateTime now);

    int countByUserIdAndProgressPercentage(Long userId, int progressPercentage);

    List<Enrollment> findByUserIdAndProgressPercentage(Long userId, int progressPercentage);

    int countByUserIdAndProgressPercentageGreaterThanAndProgressPercentageLessThan(Long userId, int min, int max);
}
