package es.codeurjc.scam_g18.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.codeurjc.scam_g18.model.LessonProgress;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.LessonProgressRepository;
import es.codeurjc.scam_g18.service.CourseService;
import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private CourseService courseService;

    private String createChart(Model model, String title, String type, List<String> labels, List<Double> values) {
        model.addAttribute("chartTitle", title);
        model.addAttribute("chartType", type);
        model.addAttribute("chartLabels", labels);
        model.addAttribute("chartValues", values);
        return "chart";
    }

    @GetMapping("/statistics/course-progress")
    public String courseProgress(Model model, @RequestParam Long userId) {
        int total = enrollmentRepository.findByUserId(userId).size();
        int completed = enrollmentRepository.countByUserIdAndProgressPercentage(userId, 100);
        int remaining = total - completed;
        List<String> namesList = new ArrayList<>();
        namesList.add("Completados");
        namesList.add("En progreso");
        List<Double> data = new ArrayList<>();
        data.add((double) completed);
        data.add((double) remaining);
        return createChart(model, "Mis Cursos", "doughnut", namesList, data);
    }

    @GetMapping("/statistics/lessons-learned")
    public String lessonsLearned(Model model, @RequestParam Long userId) {
        List<LessonProgress> completedLessons = lessonProgressRepository.findByUserIdAndIsCompletedTrue(userId);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Loop from 5 months ago to today (6 bars)
        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i);
            String monthLabel = monthDate.getMonth().getDisplayName(java.time.format.TextStyle.SHORT,
                    java.util.Locale.of("es", "ES"));
            labels.add(monthLabel);

            long count = completedLessons.stream()
                    .filter(lp -> lp.getCompletedAt() != null &&
                            lp.getCompletedAt().getMonth() == monthDate.getMonth() &&
                            lp.getCompletedAt().getYear() == monthDate.getYear())
                    .count();
            values.add((double) count);
        }
        return createChart(model, "Lecciones aprendidas", "bar", labels, values);
    }

    @GetMapping("/statistics/course-genders")
    public String courseGenders(Model model, @RequestParam Long courseId) {
        java.util.Map<String, Long> genreCount = courseService.getNumberGenre(courseId);
        List<String> labels = java.util.Arrays.asList("Hombres", "Mujeres");
        List<Double> values = new ArrayList<>();
        values.add(genreCount.getOrDefault("MALE", 0L).doubleValue());
        values.add(genreCount.getOrDefault("FEMALE", 0L).doubleValue());

        return createChart(model, "Géneros inscritos", "pie", labels, values);
    }

    @GetMapping("/statistics/course-ages")
    public String courseAges(Model model, @RequestParam Long courseId) {
        List<Long> ages = courseService.getAgesCourse(courseId);
        List<String> labels = java.util.Arrays.asList("18-25", "26-35", "36-50", "+50");
        List<Double> values = new ArrayList<>();
        if (ages != null && ages.size() >= 4) {
            for (Long count : ages) {
                values.add(count.doubleValue());
            }
        } else {
            values.addAll(java.util.Collections.nCopies(4, 0.0));
        }

        return createChart(model, "Edades de estudiantes", "bar", labels, values);
    }

    @GetMapping("/statistics/course-tags")
    public String courseTags(Model model, @RequestParam Long userId) {
        List<es.codeurjc.scam_g18.model.Tag> tags = courseService.getCommonTags(userId);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        // Simular relevancia decreciente ya que solo devolvemos los 3 tags ordenados
        for (int i = 0; i < tags.size(); i++) {
            labels.add(tags.get(i).getName());
            values.add((double) (3 - i));
        }

        return createChart(model, "Tags en común contigo", "bar", labels, values);
    }

    @GetMapping("/statistics/course-user-progress")
    public String courseUserProgress(Model model, @RequestParam Long courseId, @RequestParam Long userId) {
        long totalLessons = courseService.getTotalLessons(courseId);

        List<LessonProgress> completed = lessonProgressRepository.findByUserIdAndIsCompletedTrue(userId);
        long completedLessons = completed.stream()
                .filter(lp -> lp.getLesson() != null
                        && lp.getLesson().getModule() != null
                        && lp.getLesson().getModule().getCourse() != null
                        && lp.getLesson().getModule().getCourse().getId().equals(courseId))
                .count();

        long remaining = totalLessons - completedLessons;
        if (remaining < 0)
            remaining = 0;

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        labels.add("Completadas");
        values.add((double) completedLessons);

        labels.add("Pendientes");
        values.add((double) remaining);

        return createChart(model, "Tu progreso", "pie", labels, values);
    }

    @GetMapping("/statistics/created-course-status")
    public String createdCourseStatus(Model model, @RequestParam Long courseId) {
        Map<String, Integer> stats = courseService.getCourseCompletionStats(courseId);

        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        labels.add("Completado");
        values.add(stats.getOrDefault("completed", 0).doubleValue());

        labels.add("En progreso");
        values.add(stats.getOrDefault("inProgress", 0).doubleValue());

        return createChart(model, "Estado de estudiantes", "pie", labels, values);
    }
}
