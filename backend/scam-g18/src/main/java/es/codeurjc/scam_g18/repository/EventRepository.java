package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT DISTINCT e FROM Event e LEFT JOIN e.tags t WHERE " +
            "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND "
            +
            "(:tags IS NULL OR t.name IN :tags)")
    List<Event> findByKeywordAndTags(@Param("keyword") String keyword, @Param("tags") List<String> tags);

    List<Event> findByStatus(Status status);

    List<Event> findByTitleContainingIgnoreCase(String title);
}
