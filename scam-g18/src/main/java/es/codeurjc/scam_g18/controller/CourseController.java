package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.ReviewService;
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

    @Autowired
    private ReviewService reviewService;

    public CourseController(CourseService courseService, TagService tagService) {
        this.courseService = courseService;
        this.tagService = tagService;
    }

    @GetMapping("/courses")
    public String courses(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        model.addAttribute("courses", courseService.getCoursesViewData(search, tags, currentUserId));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));
        return "courses";
    }

    @GetMapping("/course/{id}")
    public String showCourse(Model model, @PathVariable long id, Principal principal) {
        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }

        var detailData = courseService.getCourseDetailViewData(id);
        if (detailData == null) {
            return "redirect:/courses";
        }

        boolean canManage = false;
        boolean isSuscribed = false;
        boolean hasReviewed = false;
        if (principal != null) {
            var currentUserOpt = userService.getCurrentAuthenticatedUser();
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                canManage = courseService.canManageCourse(course, currentUser);
                isSuscribed = userService.isSuscribedToCourse(currentUser.getId(), id);
                hasReviewed = reviewService.hasUserReviewed(currentUser.getId(), id);
            }
        }

        model.addAllAttributes(detailData);
        model.addAttribute("canEdit", canManage);
        model.addAttribute("isSuscribedToCourse", isSuscribed);
        model.addAttribute("hasReviewed", hasReviewed);

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

    @GetMapping("/course/{id}/edit")
    public String editCourseForm(Model model, @PathVariable long id) {
        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty() || !courseService.canManageCourse(course, currentUserOpt.get())) {
            return "redirect:/courses";
        }

        if (course.getPriceCents() != null) {
            course.setPrice(course.getPriceCents() / 100.0);
        }

        model.addAttribute("course", course);
        return "editCourse";
    }

    @PostMapping("/course/{id}/edit")
    public String updateCourse(
            @PathVariable long id,
            Course courseUpdate,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(name = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {

        if (hasInvalidCourseData(courseUpdate)) {
            return "redirect:/course/" + id + "/edit";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            boolean updated = courseService.updateCourseIfAuthorized(id, courseUpdate, currentUserOpt.get(), imageFile,
                    tagNames);
            if (updated) {
                return "redirect:/course/" + id;
            }
        }

        return "redirect:/courses";
    }

    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable long id) {
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            boolean deleted = courseService.deleteCourseIfAuthorized(id, currentUserOpt.get());
            if (deleted) {
                return "redirect:/courses";
            }
        }
        return "redirect:/course/" + id;
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

        if (hasInvalidCourseData(course)) {
            return "redirect:/courses/new";
        }

        var creatorOpt = userService.getCurrentAuthenticatedUser();
        if (creatorOpt.isEmpty()) {
            return "redirect:/login";
        }

        courseService.createCourse(course, tagNames, creatorOpt.get(), imageFile);

        return "redirect:/courses";
    }

    @PostMapping("/course/{id}/review")
    public String addReview(@PathVariable long id,
            @RequestParam int rating,
            @RequestParam String content,
            Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }

        User currentUser = currentUserOpt.get();
        reviewService.addReview(currentUser, course, rating, content);

        return "redirect:/course/" + id + "#reviews";
    }

    private boolean hasInvalidCourseData(Course course) {
        if (course == null) {
            return true;
        }
        if (course.getTitle() == null || course.getTitle().isBlank()) {
            return true;
        }
        if (course.getShortDescription() == null || course.getShortDescription().isBlank()) {
            return true;
        }
        if (course.getLongDescription() == null || course.getLongDescription().isBlank()) {
            return true;
        }
        if (course.getLanguage() == null || course.getLanguage().isBlank()) {
            return true;
        }
        if (course.getPrice() == null || course.getPrice() < 0) {
            return true;
        }
        return false;
    }

}
