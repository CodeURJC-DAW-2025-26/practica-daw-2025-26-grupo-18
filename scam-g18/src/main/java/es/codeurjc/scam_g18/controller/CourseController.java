package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.UserService;
import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.User;

import java.security.Principal;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.scam_g18.service.TagService;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    public CourseController(CourseService courseService, TagService tagService) {
        this.courseService = courseService;
        this.tagService = tagService;
    }

    @GetMapping("/courses")
    public String courses(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        model.addAttribute("courses", courseService.getCoursesViewData(search, tags));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));
        

        

        return "courses";
    }

    @GetMapping("/course/{id}")
    public String showCourse(Model model, @PathVariable long id) {
        var detailData = courseService.getCourseDetailViewData(id);
        if (detailData == null) {
            return "redirect:/courses";
        }

        model.addAllAttributes(detailData);

        return "course";
    }

    @GetMapping("/courses/subscribed")
    public String subscribedCourses(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.getCurrentAuthenticatedUser().orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        var subscribedCourses = courseService.getSubscribedCoursesViewData(user.getId());
        model.addAttribute("courses", subscribedCourses);
        model.addAttribute("hasSubscribedCourses", !subscribedCourses.isEmpty());
        model.addAttribute("userName", user.getUsername());

        return "subscribedCourses";
    }


    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        return "createCourse";
    }

    @PostMapping("/courses/new")
    public String createCourse(
            Course course,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(name = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {

        if (course.getTitle() == null || course.getTitle().isBlank()) {
            return "redirect:/courses/new";
        }
        if (course.getShortDescription() == null || course.getShortDescription().isBlank()) {
            return "redirect:/courses/new";
        }
        if (course.getLongDescription() == null || course.getLongDescription().isBlank()) {
            return "redirect:/courses/new";
        }
        if (course.getLanguage() == null || course.getLanguage().isBlank()) {
            return "redirect:/courses/new";
        }
        if (course.getPrice() == null || course.getPrice() < 0) {
            return "redirect:/courses/new";
        }

        var creatorOpt = userService.getCurrentAuthenticatedUser();
        if (creatorOpt.isEmpty()) {
            return "redirect:/login";
        }

        courseService.createCourse(course, tagNames, creatorOpt.get(), imageFile);

        return "redirect:/courses";
    }

}
