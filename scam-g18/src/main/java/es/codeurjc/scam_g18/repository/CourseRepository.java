package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCreatorId(Long creatorId);

    List<Course> findByStatus(Status status);

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c ORDER BY AVG(r.rating) DESC")
    List<Course> findTopRated(Pageable pageable);
}
