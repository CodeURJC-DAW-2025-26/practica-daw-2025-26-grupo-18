package es.codeurjc.scam_g18.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.scam_g18.dto.CourseDTO;
import es.codeurjc.scam_g18.dto.CourseMapper;
import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.ReviewService;
import es.codeurjc.scam_g18.service.UserService;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/")
    public ResponseEntity<List<Map<String, Object>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        List<Map<String, Object>> courses = courseService.getCoursesViewData(search, tags, currentUserId, page, PAGE_SIZE);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/subscribed")
    public ResponseEntity<List<Map<String, Object>>> subscribedCourses() {
        var userOpt = userService.getCurrentAuthenticatedUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var subscribedCourses = courseService.getSubscribedCoursesViewData(userOpt.get().getId());
        return ResponseEntity.ok(subscribedCourses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCourse(@PathVariable long id) {
        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        Long currentUserId = null;
        boolean canManage = false;
        boolean isSuscribed = false;
        boolean hasReviewed = false;

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isPresent()) {
            User currentUser = currentUserOpt.get();
            currentUserId = currentUser.getId();
            canManage = courseService.canManageCourse(course, currentUser);
            isSuscribed = userService.isSuscribedToCourse(currentUser.getId(), id);
            hasReviewed = reviewService.hasUserReviewed(currentUser.getId(), id);
        }

        var detailData = courseService.getCourseDetailViewData(id, currentUserId);
        if (detailData == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> responseData = new HashMap<>(detailData);
        responseData.put("canEdit", canManage);
        responseData.put("isSuscribedToCourse", isSuscribed);
        responseData.put("canAccessLessonVideos", isSuscribed || canManage);
        responseData.put("hasReviewed", hasReviewed);
        responseData.put("userId", currentUserId);

        return ResponseEntity.ok(responseData);
    }

    @GetMapping("/{courseId}/lesson/{lessonId}/video")
    public ResponseEntity<Map<String, String>> getLessonVideoUrl(
            @PathVariable long courseId, 
            @PathVariable long lessonId) {
            
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User currentUser = currentUserOpt.get();

        Course course;
        try {
            course = courseService.getCourseById(courseId);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        boolean canManage = courseService.canManageCourse(course, currentUser);

        var videoUrlOpt = canManage
                ? courseService.getLessonVideoUrl(courseId, lessonId)
                : courseService.getLessonVideoUrlIfSubscribed(courseId, lessonId, currentUser.getId());
                
        if (videoUrlOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "No access to this video"));
        }

        return ResponseEntity.ok(Map.of("videoUrl", videoUrlOpt.get()));
    }

    @PostMapping("/{courseId}/lesson/{lessonId}/complete")
    public ResponseEntity<Map<String, Object>> markLessonAsCompleted(
            @PathVariable long courseId,
            @PathVariable long lessonId) {

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = currentUserOpt.get().getId();
        boolean completed = courseService.markLessonAsCompleted(courseId, lessonId, userId);
        
        if (!completed) {
            return ResponseEntity.badRequest().body(Map.of("error", "Could not complete lesson"));
        }

        int progressPercentage = courseService.getProgressPercentageForUserCourse(courseId, userId).orElse(0);

        Map<String, Object> response = new HashMap<>();
        response.put("completed", true);
        response.put("lessonId", lessonId);
        response.put("courseId", courseId);
        response.put("progressPercentage", progressPercentage);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> createCourse(
            @RequestPart("course") CourseDTO courseDTO,
            @RequestParam(required = false) List<String> tagNames,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        var creatorOpt = userService.getCurrentAuthenticatedUser();
        if (creatorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Course course = courseMapper.toDomain(courseDTO);

        String validationError = courseService.validateCourseData(course, tagNames, imageFile, true, false);

        if (validationError != null && !validationError.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        try {
            courseService.createCourse(course, tagNames, creatorOpt.get(), imageFile);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                    .buildAndExpand(course.getId()).toUri();
            return ResponseEntity.created(location).body(courseMapper.toDTO(course));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error creating course: " + e.getMessage()));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> updateCourse(
            @PathVariable long id,
            @RequestPart("course") CourseDTO courseDTO,
            @RequestParam(required = false) List<String> tagNames,
            @RequestPart(value = "imageFile", required = false) MultipartFile imageFile) {

        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Course courseUpdate = courseMapper.toDomain(courseDTO);

        String validationError = courseService.validateCourseData(courseUpdate, tagNames, imageFile, false, false);

        if (validationError != null && !validationError.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", validationError));
        }

        try {
            boolean updated = courseService.updateCourseIfAuthorized(id, courseUpdate, currentUserOpt.get(), imageFile, tagNames);
            if (updated) {
                Course updatedCourse = courseService.getCourseById(id);
                return ResponseEntity.ok(courseMapper.toDTO(updatedCourse));
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized to update this course"));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error updating course: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCourse(@PathVariable long id) {
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean deleted = courseService.deleteCourseIfAuthorized(id, currentUserOpt.get());
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "Not authorized to delete this course"));
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Object> addReview(
            @PathVariable long id,
            @RequestParam int rating,
            @RequestParam String content) {
            
        var currentUserOpt = userService.getCurrentAuthenticatedUser();
        if (currentUserOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        User currentUser = currentUserOpt.get();
        try {
            reviewService.addReview(currentUser, course, rating, content);
            return ResponseEntity.ok(Map.of("message", "Review added successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Error adding review: " + e.getMessage()));
        }
    }
}
