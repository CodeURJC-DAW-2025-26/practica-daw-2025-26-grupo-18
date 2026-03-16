package es.codeurjc.scam_g18.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.EnrollmentService;
@Controller
public class StatisticsWebController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private CourseService courseService;

    // Helper: populates model with chart data and returns the chart view.
    private String createChart(Model model, String title, String type, List<String> labels, List<Double> values) {
        model.addAttribute("chartTitle", title);
        model.addAttribute("chartType", type);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartValues", values);
        return "chart";
    }

    @GetMapping("/statistics/course-progress")
    public String courseProgress(Model model, @RequestParam Long userId) {
        Map<String, Integer> stats = enrollmentService.getCourseProgressStats(userId);
        List<String> labels = List.of("Completados", "En progreso");
        List<Double> values = List.of(
                stats.get("completed").doubleValue(),
                stats.get("remaining").doubleValue());
        return createChart(model, "Mis Cursos", "doughnut", labels, values);
    }

    @GetMapping("/statistics/lessons-learned")
    public String lessonsLearned(Model model, @RequestParam Long userId) {
        Map<String, Object> data = enrollmentService.getLessonsPerMonth(userId);
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) data.get("labels");
        @SuppressWarnings("unchecked")
        List<Double> values = (List<Double>) data.get("values");
        return createChart(model, "Lecciones aprendidas", "bar", labels, values);
    }

    @GetMapping("/statistics/course-genders")
    public String courseGenders(Model model, @RequestParam Long courseId) {
        Map<String, Long> genreCount = courseService.getNumberGenre(courseId);
        List<String> labels = List.of("Hombres", "Mujeres");
        List<Double> values = List.of(
                genreCount.getOrDefault("MALE", 0L).doubleValue(),
                genreCount.getOrDefault("FEMALE", 0L).doubleValue());
        return createChart(model, "Géneros inscritos", "pie", labels, values);
    }

    @GetMapping("/statistics/course-ages")
    public String courseAges(Model model, @RequestParam Long courseId) {
        List<String> labels = List.of("18-25", "26-35", "36-50", "+50");
        List<Double> values = courseService.getAgesCourse(courseId);
        return createChart(model, "Edades de estudiantes", "bar", labels, values);
    }

    @GetMapping("/statistics/course-tags")
    public String courseTags(Model model, @RequestParam Long userId, @RequestParam Long courseId) {
        List<Entry<String, Integer>> tags = courseService.getCommonTags(userId, courseId);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        for (Entry<String, Integer> tag : tags) {
            labels.add(tag.getKey());
            values.add(tag.getValue().doubleValue());
        }
        return createChart(model, "Tags en común contigo", "bar", labels, values);
    }

    @GetMapping("/statistics/course-user-progress")
    public String courseUserProgress(Model model, @RequestParam Long courseId, @RequestParam Long userId) {
        long totalLessons = courseService.getTotalLessons(courseId);
        Map<String, Long> progress = enrollmentService.getUserLessonProgressForCourse(userId, courseId, totalLessons);
        List<String> labels = List.of("Completadas", "Pendientes");
        List<Double> values = List.of(
                progress.get("completedLessons").doubleValue(),
                progress.get("remaining").doubleValue());
        return createChart(model, "Tu progreso", "pie", labels, values);
    }

    @GetMapping("/statistics/created-course-status")
    public String createdCourseStatus(Model model, @RequestParam Long courseId) {
        Map<String, Integer> stats = courseService.getCourseCompletionStats(courseId);
        List<String> labels = List.of("Completado", "En progreso");
        List<Double> values = List.of(
                stats.getOrDefault("completed", 0).doubleValue(),
                stats.getOrDefault("inProgress", 0).doubleValue());
        return createChart(model, "Estado de estudiantes", "pie", labels, values);
    }
}
