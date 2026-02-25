package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.LessonProgress;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

	boolean existsByUserIdAndLessonId(Long userId, Long lessonId);

	boolean existsByUserIdAndLessonIdAndIsCompletedTrue(Long userId, Long lessonId);

	Optional<LessonProgress> findByUserIdAndLessonId(Long userId, Long lessonId);

	long countByUserIdAndIsCompletedTrue(Long userId);

	long countByUserIdAndLessonModuleCourseIdAndIsCompletedTrue(Long userId, Long courseId);

	@Query("SELECT lp.lesson.id FROM LessonProgress lp WHERE lp.user.id = :userId AND lp.lesson.module.course.id = :courseId AND lp.isCompleted = true")
	Set<Long> findCompletedLessonIdsByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

	@Modifying
	@Query("DELETE FROM LessonProgress lp WHERE lp.lesson.module.course.id = :courseId")
	int deleteByCourseId(@Param("courseId") Long courseId);
}
