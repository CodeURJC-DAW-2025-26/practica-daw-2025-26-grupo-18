package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findTop6ByOrderByRatingDesc();
}
