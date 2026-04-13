package es.codeurjc.scam_g18.controller;

import java.util.List;

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
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import es.codeurjc.scam_g18.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@Tag(name = "Course Web API", description = "AJAX endpoints used by course web pages")
public class CourseWebController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private CourseService courseService;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    // Builds the controller with main course and tag services.
    public CourseWebController(CourseService courseService, TagService tagService) {
        this.courseService = courseService;
        this.tagService = tagService;
    }

    // Displays the course listing with search and tag filtering.
    @GetMapping("/courses")
    public String courses(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        model.addAttribute("courses", courseService.getCoursesViewData(search, tags, currentUserId, 0, PAGE_SIZE));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));
        model.addAttribute("hasMore", courseService.getTotalPublishedCoursesCount(search, tags) > PAGE_SIZE);
        return "courses";
    }

    // AJAX endpoint for course pagination
    @GetMapping("/api/courses")
    @Operation(summary = "List courses (web)", description = "Returns paginated courses for the public web listing via AJAX.")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCoursesApi(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        List<Map<String, Object>> courses = courseService.getCoursesViewData(search, tags, currentUserId, page,
                PAGE_SIZE);
        return ResponseEntity.ok(courses);
    }

    // Displays course detail plus user progress/review data.
    @GetMapping("/course/{id}")
    public String showCourse(Model model, @PathVariable long id, Principal principal) {
        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }

        Long currentUserId = null;

        boolean canManage = false;
        boolean isSuscribed = false;
        boolean hasReviewed = false;
        if (principal != null) {
            var currentUserOpt = userService.getCurrentAuthenticatedUser();
            if (currentUserOpt.isPresent()) {
                User currentUser = currentUserOpt.get();
                currentUserId = currentUser.getId();
                canManage = courseService.canManageCourse(course, currentUser);
                isSuscribed = userService.isSuscribedToCourse(currentUser.getId(), id);
                hasReviewed = reviewService.hasUserReviewed(currentUser.getId(), id);
            }
        }

        var detailData = courseService.getCourseDetailViewData(id, currentUserId);
        if (detailData == null) {
            return "redirect:/courses";
        }

        model.addAllAttributes(detailData);
        model.addAttribute("canEdit", canManage);
        model.addAttribute("isSuscribedToCourse", isSuscribed);
        model.addAttribute("canAccessLessonVideos", isSuscribed || canManage);
        model.addAttribute("hasReviewed", hasReviewed);
        model.addAttribute("userId", currentUserId);

        return "course";

    }

    // Allows lesson video access to subscribers, admins, and the course creator.
    @GetMapping("/course/{courseId}/lesson/{lessonId}/video")
    public String openLessonVideo(@PathVariable long courseId, @PathVariable long lessonId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        User currentUser = currentUserOpt.get();

        Course course;
        try {
            course = courseService.getCourseById(courseId);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }

        boolean canManage = courseService.canManageCourse(course, currentUser);

        var videoUrlOpt = canManage
                ? courseService.getLessonVideoUrl(courseId, lessonId)
                : courseService.getLessonVideoUrlIfSubscribed(courseId, lessonId, currentUser.getId());
        if (videoUrlOpt.isEmpty()) {
            return "redirect:/course/" + courseId;
        }

        return "redirect:" + videoUrlOpt.get();
    }

    // Marks a lesson as completed and redirects to course detail.
    @PostMapping("/course/{courseId}/lesson/{lessonId}/complete")
    public String markLessonAsCompleted(@PathVariable long courseId, @PathVariable long lessonId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return "redirect:/login";
        }

        courseService.markLessonAsCompleted(courseId, lessonId, currentUserOpt.get().getId());
        return "redirect:/course/" + courseId;
    }

    // Marks a lesson as completed via AJAX and returns updated progress.
    @PostMapping("/course/{courseId}/lesson/{lessonId}/complete-ajax")
    @Operation(summary = "Complete lesson (web AJAX)", description = "Marks a lesson as completed and returns updated progress for web clients.")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> markLessonAsCompletedAjax(
            @PathVariable long courseId,
            @PathVariable long lessonId,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = currentUserOpt.get().getId();
        boolean completed = courseService.markLessonAsCompleted(courseId, lessonId, userId);
        if (!completed) {
            return ResponseEntity.badRequest().build();
        }

        int progressPercentage = courseService.getProgressPercentageForUserCourse(courseId, userId).orElse(0);

        Map<String, Object> response = new HashMap<>();
        response.put("completed", true);
        response.put("lessonId", lessonId);
        response.put("courseId", courseId);
        response.put("progressPercentage", progressPercentage);

        return ResponseEntity.ok(response);
    }

    // Displays courses to which the authenticated user is subscribed.
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

    // Displays the course edit form if the user has permissions.
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

        java.util.List<String> selectedTags = courseService.prepareEditData(course);
        model.addAttribute("allTagsView", tagService.getTagsView(selectedTags));
        model.addAttribute("course", course);
        return "editCourse";
    }

    // Updates an existing course when the user is authorized.
    @PostMapping("/course/{id}/edit")
    public String updateCourse(
            @PathVariable long id,
            Course courseUpdate,
            org.springframework.validation.BindingResult bindingResult,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(name = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws java.io.IOException, java.sql.SQLException {

        String validationError = courseService.validateCourseData(courseUpdate, tagNames, imageFile, false,
                bindingResult.hasErrors());

        if (validationError != null && !validationError.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", validationError);
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

    // Deletes a course if the current user has management permissions.
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

    // Displays the form to create a new course.
    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("allTagsView", tagService.getTagsView(null));
        return "createCourse";
    }

    // Creates a new course with its tags and associated image.
    @PostMapping("/courses/new")
    public String createCourse(
            Course course,
            org.springframework.validation.BindingResult bindingResult,
            @RequestParam(required = false) List<String> tagNames,
            @RequestParam(name = "imageFile", required = false) org.springframework.web.multipart.MultipartFile imageFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws java.io.IOException, java.sql.SQLException {

        String validationError = courseService.validateCourseData(course, tagNames, imageFile, true,
                bindingResult.hasErrors());

        if (validationError != null && !validationError.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", validationError);
            return "redirect:/courses/new";
        }

        var creatorOpt = userService.getCurrentAuthenticatedUser();
        if (creatorOpt.isEmpty()) {
            return "redirect:/login";
        }

        courseService.createCourse(course, tagNames, creatorOpt.get(), imageFile);

        return "redirect:/courses";
    }

    // Adds a course review from the authenticated user.
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

}
