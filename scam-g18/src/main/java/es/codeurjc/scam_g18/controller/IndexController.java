package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.model.Course;
import java.util.List;

@Controller
public class IndexController {

    private final CourseService courseService;

    public IndexController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Course> featuredCourses = courseService.getFeaturedCourses(); // 7 cursos
        model.addAttribute("courses", featuredCourses);

        return "index";
    }

    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        return "error";
    }
}
