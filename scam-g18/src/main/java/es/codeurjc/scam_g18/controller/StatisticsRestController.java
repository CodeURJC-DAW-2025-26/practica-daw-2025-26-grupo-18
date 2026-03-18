package es.codeurjc.scam_g18.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.dto.ChartDTO;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.EnrollmentService;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsRestController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    // Helper: builds a ChartDTO JSON response.
    private ResponseEntity<ChartDTO> chart(String title, String type, List<String> labels, List<Double> values) {
        return ResponseEntity.ok(new ChartDTO(title, type, labels, values));
    }

    @GetMapping("/course-progress")
    public ResponseEntity<ChartDTO> courseProgress(@RequestParam Long userId) {
        Map<String, Integer> stats = enrollmentService.getCourseProgressStats(userId);
        List<String> labels = List.of("Completados", "En progreso");
        List<Double> values = List.of(
                stats.get("completed").doubleValue(),
                stats.get("remaining").doubleValue());
        return chart("Mis Cursos", "doughnut", labels, values);
    }

    @GetMapping("/lessons-learned")
    public ResponseEntity<ChartDTO> lessonsLearned(@RequestParam Long userId) {
        Map<String, Object> data = enrollmentService.getLessonsPerMonth(userId);
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) data.get("labels");
        @SuppressWarnings("unchecked")
        List<Double> values = (List<Double>) data.get("values");
        return chart("Lecciones aprendidas", "bar", labels, values);
    }

    @GetMapping("/course-genders")
    public ResponseEntity<ChartDTO> courseGenders(@RequestParam Long courseId) {
        Map<String, Long> genreCount = courseService.getNumberGenre(courseId);
        List<String> labels = List.of("Hombres", "Mujeres");
        List<Double> values = List.of(
                genreCount.getOrDefault("MALE", 0L).doubleValue(),
                genreCount.getOrDefault("FEMALE", 0L).doubleValue());
        return chart("Géneros inscritos", "pie", labels, values);
    }

    @GetMapping("/course-ages")
    public ResponseEntity<ChartDTO> courseAges(@RequestParam Long courseId) {
        List<String> labels = List.of("18-25", "26-35", "36-50", "+50");
        List<Double> values = courseService.getAgesCourse(courseId);
        return chart("Edades de estudiantes", "bar", labels, values);
    }

    @GetMapping("/course-tags")
    public ResponseEntity<ChartDTO> courseTags(@RequestParam Long userId, @RequestParam Long courseId) {
        List<Entry<String, Integer>> tags = courseService.getCommonTags(userId, courseId);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Entry<String, Integer> tag : tags) {
            labels.add(tag.getKey());
            values.add(tag.getValue().doubleValue());
        }
        return chart("Tags en común contigo", "bar", labels, values);
    }

    @GetMapping("/course-user-progress")
    public ResponseEntity<ChartDTO> courseUserProgress(@RequestParam Long courseId, @RequestParam Long userId) {
        long totalLessons = courseService.getTotalLessons(courseId);
        Map<String, Long> progress = enrollmentService.getUserLessonProgressForCourse(userId, courseId, totalLessons);
        List<String> labels = List.of("Completadas", "Pendientes");
        List<Double> values = List.of(
                progress.get("completedLessons").doubleValue(),
                progress.get("remaining").doubleValue());
        return chart("Tu progreso", "pie", labels, values);
    }

    @GetMapping("/created-course-status")
    public ResponseEntity<ChartDTO> createdCourseStatus(@RequestParam Long courseId) {
        Map<String, Integer> stats = courseService.getCourseCompletionStats(courseId);
        List<String> labels = List.of("Completado", "En progreso");
        List<Double> values = List.of(
                stats.getOrDefault("completed", 0).doubleValue(),
                stats.getOrDefault("inProgress", 0).doubleValue());
        return chart("Estado de estudiantes", "pie", labels, values);
    }
}
