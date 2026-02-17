package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable; // Correct import

public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c ORDER BY AVG(r.rating) DESC")
    List<Course> findTopRated(Pageable pageable);
}
