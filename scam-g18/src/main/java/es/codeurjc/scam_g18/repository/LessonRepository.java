package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
