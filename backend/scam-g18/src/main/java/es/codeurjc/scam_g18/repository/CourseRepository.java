package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
        List<Course> findByCreatorId(Long creatorId);

        List<Course> findByTitleContainingIgnoreCase(String title);

        List<Course> findByStatus(Status status);

        @Query("SELECT c FROM Course c LEFT JOIN c.reviews r GROUP BY c ORDER BY AVG(r.rating) DESC")
        List<Course> findTopRated(Pageable pageable);

        @Query("SELECT DISTINCT c FROM Course c LEFT JOIN c.tags t WHERE " +
                        "(:keyword IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.shortDescription) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
                        +
                        "(:tags IS NULL OR t.name IN :tags)")
        List<Course> findByKeywordAndTags(@org.springframework.data.repository.query.Param("keyword") String keyword,
                        @org.springframework.data.repository.query.Param("tags") List<String> tags);
}
