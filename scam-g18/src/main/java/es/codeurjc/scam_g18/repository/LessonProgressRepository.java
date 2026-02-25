package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

	@Modifying
	@Query("DELETE FROM LessonProgress lp WHERE lp.lesson.module.course.id = :courseId")
	int deleteByCourseId(@Param("courseId") Long courseId);
}
