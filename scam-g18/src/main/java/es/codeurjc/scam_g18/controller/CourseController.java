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
import org.springframework.web.bind.annotation.PathVariable;

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
            courseData.put("description", course.getShortDescription());
            courseData.put("language", course.getLanguage());
            courseData.put("priceInEuros", courseService.getPriceInEuros(course));
            courseData.put("creatorUsername",
                    course.getCreator() != null ? course.getCreator().getUsername() : "Desconocido");
            courseData.put("averageRating", String.format("%.1f", courseService.getAverageRating(course)));
            courseData.put("ratingCount", courseService.getRatingCount(course));
            courseData.put("tags", course.getTags());
            enrichedCourses.add(courseData);
        }

        model.addAttribute("courses", enrichedCourses);

        return "courses";
    }

    @GetMapping("/course/{id}")
    public String course(Model model, @PathVariable long id) {
        Course course = courseService.getCourseById(id);

        // Pasamos el objeto curso completo
        model.addAttribute("course", course);

        // Pasamos los atributos calculados o extra que no están directamente en la
        // entidad
        model.addAttribute("modules", course.getModules());
        model.addAttribute("priceInEuros", courseService.getPriceInEuros(course));
        model.addAttribute("averageRatingStars", courseService.getStarsFromAverage(course));
        model.addAttribute("averageRating", String.format("%.1f", courseService.getAverageRating(course)));
        model.addAttribute("ratingCount", courseService.getRatingCount(course));
        model.addAttribute("reviewsNumber", courseService.getReviewsNumber(course));

        return "course";
    }

}
