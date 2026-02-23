package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.CourseService;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;
import es.codeurjc.scam_g18.service.TagService;

@Controller
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private TagService tagService;

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

    @GetMapping("/courses/new")
    public String newCourseForm(@RequestParam Model model) {
        return "createCourse";
    }


    

}
