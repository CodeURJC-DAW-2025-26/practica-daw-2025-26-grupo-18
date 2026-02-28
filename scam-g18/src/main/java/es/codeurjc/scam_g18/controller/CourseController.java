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

    // Construye el controlador con los servicios principales de cursos y etiquetas.
    public CourseController(CourseService courseService, TagService tagService) {
        this.courseService = courseService;
        this.tagService = tagService;
    }

    // Muestra el listado de cursos con búsqueda y filtro por etiquetas.
    @GetMapping("/courses")
    public String courses(Model model, @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> tags) {
        Long currentUserId = userService.getCurrentAuthenticatedUser().map(User::getId).orElse(null);
        model.addAttribute("courses", courseService.getCoursesViewData(search, tags, currentUserId));
        model.addAttribute("search", search);
        model.addAttribute("tagsView", tagService.getTagsView(tags));
        return "courses";
    }

    // Muestra el detalle de un curso y los datos de progreso/reseñas del usuario.
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

    // Permite abrir el vídeo de una lección a suscritos, administradores y creador del curso.
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

    // Marca una lección como completada y redirige al detalle del curso.
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

    // Marca una lección como completada vía AJAX y devuelve el progreso
    // actualizado.
    @PostMapping("/course/{courseId}/lesson/{lessonId}/complete-ajax")
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

    // Muestra los cursos a los que el usuario autenticado está suscrito.
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

    // Muestra el formulario de edición de un curso si el usuario tiene permisos.
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

        java.util.List<String> selectedTags = course.getTags().stream().map(es.codeurjc.scam_g18.model.Tag::getName)
                .toList();
        model.addAttribute("allTagsView", tagService.getTagsView(selectedTags));

        model.addAttribute("course", course);
        return "editCourse";
    }

    // Actualiza un curso existente cuando el usuario está autorizado.
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

    // Elimina un curso si el usuario actual tiene permisos de gestión.
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

    // Muestra el formulario para crear un nuevo curso.
    @GetMapping("/courses/new")
    public String newCourseForm(Model model) {
        model.addAttribute("course", new Course());
        model.addAttribute("allTagsView", tagService.getTagsView(null));
        return "createCourse";
    }

    // Crea un nuevo curso con sus etiquetas e imagen asociadas.
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

    // Añade una reseña al curso desde el usuario autenticado.
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
