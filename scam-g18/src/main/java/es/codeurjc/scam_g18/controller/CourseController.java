package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.model.Course;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

@Controller
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        // Obtener todos los cursos de la base de datos
        List<Course> allCourses = courseService.getAllCourses();

        // Enriquecer cada curso con datos calculados del servicio
        List<Map<String, Object>> enrichedCourses = new ArrayList<>();
        for (Course course : allCourses) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", course.getId());
            courseData.put("title", course.getTitle());
            courseData.put("description", course.getDescription());
            courseData.put("language", course.getLanguage());
            courseData.put("priceInEuros", courseService.getPriceInEuros(course));
            courseData.put("creatorUsername",
                    course.getCreator() != null ? course.getCreator().getUsername() : "Desconocido");
            courseData.put("averageRating", String.format("%.1f", courseService.getAverageRating(course)));
            courseData.put("ratingCount", courseService.getRatingCount(course));
            enrichedCourses.add(courseData);
        }

        model.addAttribute("courses", enrichedCourses);

        return "courses";
    }
}
