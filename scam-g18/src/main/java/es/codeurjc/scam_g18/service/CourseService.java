package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Enrollment;
import es.codeurjc.scam_g18.model.LessonProgress;
import es.codeurjc.scam_g18.model.Lesson;
import es.codeurjc.scam_g18.model.Module;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.model.OrderStatus;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.LessonProgressRepository;
import es.codeurjc.scam_g18.repository.LessonRepository;
import es.codeurjc.scam_g18.repository.OrderItemRepository;
import es.codeurjc.scam_g18.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private TagRepository tagRepository;

    // Obtiene cursos destacados para la portada.
    public List<Course> getFeaturedCourses() {
        return courseRepository.findAll();
    }

    // Obtiene todos los cursos.
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Busca cursos por palabra clave y etiquetas.
    public List<Course> searchCourses(String keyword, List<String> tags) {
        if ((keyword == null || keyword.trim().isEmpty()) && (tags == null || tags.isEmpty())) {
            return getAllCourses();
        }
        if (keyword != null && keyword.trim().isEmpty()) {
            keyword = null;
        }
        if (tags != null && tags.isEmpty()) {
            tags = null;
        }
        return courseRepository.findByKeywordAndTags(keyword, tags);
    }

    // Obtiene un curso por id o lanza excepción si no existe.
    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    // Construye los datos de listado de cursos publicados para la vista.
    public List<Map<String, Object>> getCoursesViewData(String keyword, List<String> tags, Long userId) {
        List<Course> allCourses = searchCourses(keyword, tags);
        List<Course> publishedCourses = new ArrayList<>();

        for (Course course : allCourses) {
            if (course.getStatus() == Status.PUBLISHED) {
                publishedCourses.add(course);
            }
        }

        Set<String> subscribedTagNames = getSubscribedCourseTagNames(userId);
        if (!subscribedTagNames.isEmpty()) {
            publishedCourses.sort(
                    Comparator.comparingInt((Course course) -> countMatchingTags(course.getTags(), subscribedTagNames))
                            .reversed()
                            .thenComparing(Course::getTitle, String.CASE_INSENSITIVE_ORDER));
        }

        List<Map<String, Object>> enrichedCourses = new ArrayList<>();

        for (Course course : publishedCourses) {

            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", course.getId());
            courseData.put("title", course.getTitle());
            courseData.put("description", course.getShortDescription());
            courseData.put("language", course.getLanguage());
            courseData.put("priceInEuros", getPriceInEuros(course));
            courseData.put("creatorUsername",
                    course.getCreator() != null ? course.getCreator().getUsername() : "Desconocido");
            courseData.put("averageRating", String.format("%.1f", getAverageRating(course)));
            courseData.put("ratingCount", getRatingCount(course));
            courseData.put("tags", course.getTags());
            courseData.put("subscribersNumber", course.getSubscribersNumber());
            courseData.put("videoHours", course.getVideoHours());
            courseData.put("downloadableResources", course.getDownloadableResources());
            enrichedCourses.add(courseData);
        }

        return enrichedCourses;
    }

    // Construye el detalle completo de un curso para la vista.
    public Map<String, Object> getCourseDetailViewData(Long id, Long currentUserId) {
        Course course;
        try {
            course = getCourseById(id);
        } catch (RuntimeException e) {
            return null;
        }

        Set<Long> completedLessonIds = new HashSet<>();
        if (currentUserId != null) {
            completedLessonIds = lessonProgressRepository.findCompletedLessonIdsByUserIdAndCourseId(currentUserId, id);
        }

        Map<String, Object> detailData = new HashMap<>();

        Map<String, Object> courseData = new HashMap<>();
        courseData.put("id", course.getId());
        courseData.put("title", course.getTitle());
        courseData.put("shortDescription", course.getShortDescription());
        courseData.put("longDescription", course.getLongDescription());
        courseData.put("updatedAt", course.getUpdatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        courseData.put("subscribersNumber", course.getSubscribersNumber() != null ? course.getSubscribersNumber() : 0);
        courseData.put("language", course.getLanguage());
        courseData.put("learningPoints", course.getLearningPoints());
        courseData.put("prerequisites", course.getPrerequisites());
        courseData.put("tags", course.getTags());
        courseData.put("videoHours", course.getVideoHours());
        courseData.put("downloadableResources", course.getDownloadableResources());

        Map<String, Object> creatorData = new HashMap<>();
        String creatorUsername = "Desconocido";
        if (course.getCreator() != null && course.getCreator().getUsername() != null) {
            creatorUsername = course.getCreator().getUsername();
        }
        creatorData.put("username", creatorUsername);
        courseData.put("creator", creatorData);

        Map<String, Object> imageData = new HashMap<>();
        String courseImageUrl = "/img/default_img.png";
        if (course.getId() != null) {
            courseImageUrl = "/images/courses/" + course.getId();
        }
        imageData.put("url", courseImageUrl);
        courseData.put("image", imageData);

        List<Map<String, Object>> modulesData = new ArrayList<>();
        List<Module> modules = course.getModules() != null ? course.getModules() : new ArrayList<>();
        for (int index = 0; index < modules.size(); index++) {
            Module module = modules.get(index);
            Map<String, Object> moduleData = new HashMap<>();
            moduleData.put("id", module.getId());
            moduleData.put("orderIndex", module.getOrderIndex());
            moduleData.put("title", module.getTitle());
            moduleData.put("first", index == 0);

            List<Map<String, Object>> lessonsData = new ArrayList<>();
            List<Lesson> lessons = module.getLessons() != null ? module.getLessons() : new ArrayList<>();
            for (Lesson lesson : lessons) {
                Map<String, Object> lessonData = new HashMap<>();
                lessonData.put("id", lesson.getId());
                lessonData.put("courseId", course.getId());
                lessonData.put("title", lesson.getTitle());
                lessonData.put("videoUrl", lesson.getVideoUrl());
                lessonData.put("completed", completedLessonIds.contains(lesson.getId()));
                lessonsData.add(lessonData);
            }
            moduleData.put("lessons", lessonsData);

            modulesData.add(moduleData);
        }

        List<Map<String, Object>> reviewsData = new ArrayList<>();
        List<Review> reviews = course.getReviews() != null ? course.getReviews() : new ArrayList<>();
        for (Review review : reviews) {
            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("content", review.getContent());
            reviewData.put("stars", review.getStars());

            Map<String, Object> userData = new HashMap<>();
            User user = review.getUser();
            String username = user != null && user.getUsername() != null ? user.getUsername() : "Anónimo";
            userData.put("username", username);
            userData.put("id", user != null ? user.getId() : null);
            userData.put("initials", user != null ? user.getInitials() : getInitials(username));

            if (user != null && user.getImage() != null) {
                Map<String, Object> userImageData = new HashMap<>();
                userImageData.put("url", "/images/users/" + user.getId() + "/profile");
                userData.put("image", userImageData);
            }

            reviewData.put("user", userData);
            reviewsData.add(reviewData);
        }

        detailData.put("course", courseData);
        detailData.put("modules", modulesData);
        detailData.put("reviews", reviewsData);
        detailData.put("priceInEuros", getPriceInEuros(course));
        detailData.put("averageRating", String.format("%.1f", getAverageRating(course)));
        detailData.put("ratingCount", getRatingCount(course));
        detailData.put("reviewsNumber", getReviewsNumber(course));
        detailData.put("averageRatingStars", getStarsFromAverage(course));
        int courseProgressPercentage = 0;
        if (currentUserId != null) {
            courseProgressPercentage = getProgressPercentageForUserCourse(id, currentUserId).orElse(0);
        }
        detailData.put("courseProgressPercentage", courseProgressPercentage);

        return detailData;
    }

    @Transactional
    // Marca una lección como completada y recalcula el progreso del curso.
    public boolean markLessonAsCompleted(Long courseId, Long lessonId, Long userId) {
        if (courseId == null || lessonId == null || userId == null) {
            return false;
        }

        var enrollmentOpt = enrollmentRepository.findByUserIdAndCourseId(userId, courseId);
        if (enrollmentOpt.isEmpty() || !enrollmentOpt.get().isActive()) {
            return false;
        }

        var lessonOpt = lessonRepository.findById(lessonId);
        if (lessonOpt.isEmpty()) {
            return false;
        }

        Lesson lesson = lessonOpt.get();
        if (lesson.getModule() == null || lesson.getModule().getCourse() == null
                || !courseId.equals(lesson.getModule().getCourse().getId())) {
            return false;
        }

        Enrollment enrollment = enrollmentOpt.get();

        var lessonProgressOpt = lessonProgressRepository.findByUserIdAndLessonId(userId, lessonId);
        if (lessonProgressOpt.isPresent()) {
            LessonProgress lessonProgress = lessonProgressOpt.get();
            if (!Boolean.TRUE.equals(lessonProgress.getIsCompleted())) {
                lessonProgress.setIsCompleted(true);
                lessonProgress.setCompletedAt(java.time.LocalDateTime.now());
                lessonProgressRepository.save(lessonProgress);
            }
        } else {
            LessonProgress lessonProgress = new LessonProgress();
            lessonProgress.setUser(enrollment.getUser());
            lessonProgress.setLesson(lesson);
            lessonProgress.setIsCompleted(true);
            lessonProgress.setCompletedAt(java.time.LocalDateTime.now());
            lessonProgressRepository.save(lessonProgress);
        }

        long totalLessons = lessonRepository.countByModuleCourseId(courseId);
        long completedLessons = lessonProgressRepository
                .countByUserIdAndLessonModuleCourseIdAndIsCompletedTrue(userId, courseId);

        int progressPercentage = 0;
        if (totalLessons > 0) {
            progressPercentage = (int) Math.round((completedLessons * 100.0) / totalLessons);
        }

        enrollment.setProgressPercentage(Math.min(100, progressPercentage));
        enrollmentRepository.save(enrollment);

        return true;
    }

    // Obtiene el porcentaje de progreso de un usuario en un curso.
    public java.util.Optional<Integer> getProgressPercentageForUserCourse(Long courseId, Long userId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .map(enrollment -> enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0);
    }

    // Obtiene etiquetas de cursos ya suscritos por el usuario.
    private Set<String> getSubscribedCourseTagNames(Long userId) {
        Set<String> subscribedTagNames = new HashSet<>();
        if (userId == null) {
            return subscribedTagNames;
        }

        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        for (Enrollment enrollment : enrollments) {
            Course subscribedCourse = enrollment.getCourse();
            if (subscribedCourse == null || subscribedCourse.getTags() == null) {
                continue;
            }

            for (Tag tag : subscribedCourse.getTags()) {
                if (tag != null && tag.getName() != null && !tag.getName().isBlank()) {
                    subscribedTagNames.add(tag.getName().trim().toLowerCase());
                }
            }
        }

        return subscribedTagNames;
    }

    // Cuenta coincidencias de etiquetas entre curso y preferencias del usuario.
    private int countMatchingTags(Set<Tag> candidateTags, Set<String> subscribedTagNames) {
        if (candidateTags == null || candidateTags.isEmpty() || subscribedTagNames.isEmpty()) {
            return 0;
        }

        int matches = 0;
        for (Tag tag : candidateTags) {
            if (tag == null || tag.getName() == null) {
                continue;
            }

            String normalizedName = tag.getName().trim().toLowerCase();
            if (!normalizedName.isBlank() && subscribedTagNames.contains(normalizedName)) {
                matches++;
            }
        }

        return matches;
    }

    // Construye los datos de cursos suscritos para la vista de usuario.
    public List<Map<String, Object>> getSubscribedCoursesViewData(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        List<Map<String, Object>> subscribedCourses = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            if (course == null) {
                continue;
            }

            Map<String, Object> courseData = new HashMap<>();
            courseData.put("id", course.getId());
            courseData.put("title", course.getTitle());
            courseData.put("shortDescription", course.getShortDescription());
            courseData.put("progressPercentage",
                    enrollment.getProgressPercentage() != null ? enrollment.getProgressPercentage() : 0);

            String courseImageUrl = "/img/default_img.png";
            if (course.getId() != null) {
                courseImageUrl = "/images/courses/" + course.getId();
            }
            courseData.put("imageUrl", courseImageUrl);

            subscribedCourses.add(courseData);
        }

        return subscribedCourses;
    }

    // Calcula la valoración media de un curso.
    public Double getAverageRating(Course course) {
        if (course.getReviews() == null || course.getReviews().isEmpty())
            return 0.0;
        return course.getReviews().stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(r -> r.getRating())
                .average()
                .orElse(0.0);
    }

    // Cuenta cuántas valoraciones numéricas tiene un curso.
    public Integer getRatingCount(Course course) {
        if (course.getReviews() == null)
            return 0;
        return (int) course.getReviews().stream()
                .filter(r -> r.getRating() != null)
                .count();
    }

    // Formatea el precio del curso en euros.
    public String getPriceInEuros(Course course) {
        if (course.getPriceCents() == null)
            return "0.00";
        return String.format("%.2f", course.getPriceCents() / 100.0);
    }

    // Cuenta el número de reseñas de un curso.
    public int getReviewsNumber(Course course) {
        if (course.getReviews() == null)
            return 0;
        return course.getReviews().size();
    }

    // Incrementa en uno el número de suscriptores de un curso.
    public void incrementSubscribers(Course course) {
        course.setSubscribersNumber(course.getSubscribersNumber() + 1);
        courseRepository.save(course);
    }

    public String validateCourseData(Course course, List<String> tagNames,
            org.springframework.web.multipart.MultipartFile imageFile, boolean isNewCourse, boolean hasBindingErrors) {
        List<String> errors = new ArrayList<>();

        if (hasBindingErrors) {
            errors.add(
                    "Error: Por favor, verifique que todos los campos numéricos (como precio, horas) tengan valores correctos.");
        }

        if (course == null) {
            return "Error: Datos del curso nulos.";
        }
        if (course.getTitle() == null || course.getTitle().isBlank()) {
            errors.add("Error: El título es obligatorio.");
        }
        if (course.getShortDescription() == null || course.getShortDescription().isBlank()) {
            errors.add("Error: La descripción breve es obligatoria.");
        }
        if (course.getLongDescription() == null || course.getLongDescription().isBlank()) {
            errors.add("Error: La descripción detallada es obligatoria.");
        }
        if (course.getLanguage() == null || course.getLanguage().isBlank()) {
            errors.add("Error: El idioma es obligatorio.");
        }
        if (course.getPrice() == null || course.getPrice() < 0) {
            errors.add("Error: El precio no puede ser negativo.");
        }
        if (course.getVideoHours() == null || course.getVideoHours() < 0) {
            errors.add("Error: Las horas de vídeo no pueden ser negativas.");
        }
        if (course.getDownloadableResources() == null || course.getDownloadableResources() < 0) {
            errors.add("Error: Los recursos descargables no pueden ser negativos.");
        }
        if (course.getLearningPoints() == null || course.getLearningPoints().isEmpty()) {
            errors.add("Error: Debe incluir al menos un objetivo de aprendizaje.");
        }
        if (course.getPrerequisites() == null || course.getPrerequisites().isEmpty()) {
            errors.add("Error: Debe incluir al menos un requisito previo.");
        }

        if (isNewCourse) {
            if (imageFile == null || imageFile.isEmpty()) {
                errors.add("Debe proporcionar una imagen para el curso.");
            }
            if (tagNames == null || tagNames.isEmpty()) {
                errors.add("Debe añadir al menos una etiqueta (tema relacionado).");
            }
            if (course.getModules() == null || course.getModules().isEmpty()) {
                errors.add("El curso debe contener al menos un módulo válido.");
            } else {
                for (es.codeurjc.scam_g18.model.Module module : course.getModules()) {
                    if (module.getTitle() == null || module.getTitle().isBlank()) {
                        errors.add("Todos los módulos deben tener un título válido.");
                        break;
                    }
                    if (module.getLessons() == null || module.getLessons().isEmpty()) {
                        errors.add("Cada módulo debe contener al menos una lección.");
                        break;
                    }
                    boolean lessonError = false;
                    for (es.codeurjc.scam_g18.model.Lesson lesson : module.getLessons()) {
                        if (lesson.getTitle() == null || lesson.getTitle().isBlank() || lesson.getVideoUrl() == null
                                || lesson.getVideoUrl().isBlank()) {
                            errors.add("Todas las lecciones deben tener un título y una URL de vídeo válidos.");
                            lessonError = true;
                            break;
                        }
                    }
                    if (lessonError)
                        break;
                }
            }
        }

        if (errors.isEmpty()) {
            return null;
        } else {
            return String.join("<br>", errors);
        }
    }

    // Crea un curso nuevo aplicando normalización de datos del formulario.
    public void createCourse(Course course, List<String> tagNames, User creator,
            org.springframework.web.multipart.MultipartFile imageFile)
            throws java.io.IOException, java.sql.SQLException {
        applyCommonCourseFormData(course, course, tagNames);
        if (course.getSubscribersNumber() == null) {
            course.setSubscribersNumber(0);
        }
        course.setStatus(Status.PENDING_REVIEW);
        course.setCreator(creator);

        if (imageFile != null && !imageFile.isEmpty()) {
            course.setImage(imageService.saveImage(imageFile));
        }
        courseRepository.save(course);
    }

    public List<Course> getCoursesByCreator(Long creatorId) {
        return courseRepository.findByCreatorId(creatorId);
    }

    public List<Map<String, Object>> getCreatedCoursesWithStats(Long creatorId) {
        List<Course> courses = courseRepository.findByCreatorId(creatorId);
        List<Map<String, Object>> result = new ArrayList<>();
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        for (Course c : courses) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", c.getId());
            data.put("title", c.getTitle());
            data.put("status", c.getStatus());

            List<Enrollment> enrollments = enrollmentRepository.findByCourseId(c.getId());
            data.put("totalSubscribers", enrollments.size());

            long monthly = enrollments.stream()
                    .filter(e -> e.getEnrolledAt() != null &&
                            e.getEnrolledAt().getMonth() == now.getMonth() &&
                            e.getEnrolledAt().getYear() == now.getYear())
                    .count();
            data.put("monthlySubscribers", monthly);

            result.add(data);
        }
        return result;
    }

    // Obtiene el número de usuarios suscritos y los que han terminado un curso
    public java.util.Map<String, Integer> getCourseCompletionStats(Long courseId) {
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        int totalEnrolled = enrollments.size();
        int completed = 0;

        for (Enrollment e : enrollments) {
            if (e.getProgressPercentage() != null && e.getProgressPercentage() == 100) {
                completed++;
            }
        }

        java.util.Map<String, Integer> stats = new java.util.HashMap<>();
        stats.put("total", totalEnrolled);
        stats.put("completed", completed);
        stats.put("inProgress", totalEnrolled - completed);
        return stats;
    }

    // Comprueba si un usuario puede gestionar un curso.
    public boolean canManageCourse(Course course, User user) {
        if (course == null || user == null) {
            return false;
        }

        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().equals("ADMIN"));
        boolean isCreator = course.getCreator() != null && course.getCreator().getId().equals(user.getId());
        return isAdmin || isCreator;
    }

    @Transactional
    // Actualiza un curso si el usuario tiene permisos y restaura progreso.
    public boolean updateCourseIfAuthorized(long id, Course courseUpdate, User user,
            org.springframework.web.multipart.MultipartFile imageFile, List<String> tagNames)
            throws java.io.IOException, java.sql.SQLException {
        var courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return false;
        }

        Course course = courseOpt.get();
        if (!canManageCourse(course, user)) {
            return false;
        }

        List<Enrollment> enrollmentsForCourse = enrollmentRepository.findByCourseId(course.getId());
        Map<Long, Map<String, Integer>> completedLessonKeyCountsByUser = captureCompletedLessonKeyCountsByUser(course,
                enrollmentsForCourse);

        lessonProgressRepository.deleteByCourseId(course.getId());

        applyCommonCourseFormData(course, courseUpdate, tagNames);

        if (imageFile != null && !imageFile.isEmpty()) {
            course.setImage(imageService.saveImage(imageFile));
        }

        courseRepository.save(course);
        restoreProgressAfterCourseStructureUpdate(course, enrollmentsForCourse, completedLessonKeyCountsByUser);
        enrollmentRepository.saveAll(enrollmentsForCourse);
        return true;
    }

    @Transactional
    // Elimina un curso si el usuario está autorizado.
    public boolean deleteCourseIfAuthorized(long id, User user) {
        var courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return false;
        }

        Course course = courseOpt.get();
        if (!canManageCourse(course, user)) {
            return false;
        }

        lessonProgressRepository.deleteByCourseId(course.getId());

        enrollmentRepository.deleteByCourseId(course.getId());

        orderItemRepository.deleteByCourseIdAndOrderStatus(course.getId(), OrderStatus.PENDING);
        orderItemRepository.clearCourseReferenceByCourseIdAndOrderStatusNot(course.getId(), OrderStatus.PENDING);

        courseRepository.delete(course);
        return true;
    }

    // Aplica y normaliza los datos comunes de formulario sobre un curso.
    private void applyCommonCourseFormData(Course target, Course source, List<String> tagNames) {
        target.setTitle(source.getTitle());
        target.setShortDescription(source.getShortDescription());
        target.setLongDescription(source.getLongDescription());
        target.setLanguage(source.getLanguage());
        target.setVideoHours(source.getVideoHours());

        if (source.getPrice() != null) {
            target.setPriceCents((int) (source.getPrice() * 100));
        }
        if (source.getDownloadableResources() == null) {
            target.setDownloadableResources(0);
        } else {
            target.setDownloadableResources(source.getDownloadableResources());
        }

        target.getLearningPoints().clear();
        if (source.getLearningPoints() != null) {
            source.getLearningPoints().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(target.getLearningPoints()::add);
        }

        target.getPrerequisites().clear();
        if (source.getPrerequisites() != null) {
            source.getPrerequisites().stream()
                    .filter(s -> s != null && !s.isBlank())
                    .forEach(target.getPrerequisites()::add);
        }

        target.getTags().clear();
        target.getTags().addAll(normalizeTags(tagNames));

        List<Module> normalizedModules = normalizeModules(source.getModules());
        target.getModules().clear();
        for (Module module : normalizedModules) {
            target.addModule(module);
        }
    }

    // Captura progreso histórico por claves de lección para cada usuario
    // matriculado.
    private Map<Long, Map<String, Integer>> captureCompletedLessonKeyCountsByUser(Course course,
            List<Enrollment> enrollments) {
        Map<Long, Map<String, Integer>> result = new HashMap<>();

        Map<Long, String> lessonKeyById = new HashMap<>();
        List<Module> modules = course.getModules() != null ? course.getModules() : new ArrayList<>();
        for (Module module : modules) {
            if (module.getLessons() == null) {
                continue;
            }
            for (Lesson lesson : module.getLessons()) {
                if (lesson.getId() != null) {
                    lessonKeyById.put(lesson.getId(), buildLessonCompletionKey(lesson));
                }
            }
        }

        for (Enrollment enrollment : enrollments) {
            Long userId = enrollment.getUser() != null ? enrollment.getUser().getId() : null;
            if (userId == null || course.getId() == null) {
                continue;
            }

            Set<Long> completedIds = lessonProgressRepository.findCompletedLessonIdsByUserIdAndCourseId(userId,
                    course.getId());
            Map<String, Integer> keyCounts = new HashMap<>();
            for (Long completedLessonId : completedIds) {
                String key = lessonKeyById.get(completedLessonId);
                if (key != null) {
                    keyCounts.put(key, keyCounts.getOrDefault(key, 0) + 1);
                }
            }
            result.put(userId, keyCounts);
        }

        return result;
    }

    // Restaura el progreso de lecciones tras cambios estructurales del curso.
    private void restoreProgressAfterCourseStructureUpdate(Course course,
            List<Enrollment> enrollments,
            Map<Long, Map<String, Integer>> completedLessonKeyCountsByUser) {

        List<Lesson> updatedLessons = new ArrayList<>();
        List<Module> modules = course.getModules() != null ? course.getModules() : new ArrayList<>();
        for (Module module : modules) {
            if (module.getLessons() != null) {
                updatedLessons.addAll(module.getLessons());
            }
        }

        List<LessonProgress> restoredProgressRows = new ArrayList<>();
        int totalLessons = updatedLessons.size();

        for (Enrollment enrollment : enrollments) {
            Long userId = enrollment.getUser() != null ? enrollment.getUser().getId() : null;
            if (userId == null) {
                enrollment.setProgressPercentage(0);
                continue;
            }

            Map<String, Integer> remainingByKey = new HashMap<>(
                    completedLessonKeyCountsByUser.getOrDefault(userId, new HashMap<>()));

            int completedLessons = 0;
            for (Lesson lesson : updatedLessons) {
                String key = buildLessonCompletionKey(lesson);
                int remainingMatches = remainingByKey.getOrDefault(key, 0);
                if (remainingMatches <= 0) {
                    continue;
                }

                LessonProgress restoredProgress = new LessonProgress();
                restoredProgress.setUser(enrollment.getUser());
                restoredProgress.setLesson(lesson);
                restoredProgress.setIsCompleted(true);
                restoredProgress.setCompletedAt(java.time.LocalDateTime.now());
                restoredProgressRows.add(restoredProgress);

                remainingByKey.put(key, remainingMatches - 1);
                completedLessons++;
            }

            int progressPercentage = 0;
            if (totalLessons > 0) {
                progressPercentage = (int) Math.round((completedLessons * 100.0) / totalLessons);
            }
            enrollment.setProgressPercentage(Math.max(0, Math.min(100, progressPercentage)));
        }

        if (!restoredProgressRows.isEmpty()) {
            lessonProgressRepository.saveAll(restoredProgressRows);
        }
    }

    // Genera una clave estable para identificar una lección por contenido.
    private String buildLessonCompletionKey(Lesson lesson) {
        String title = lesson.getTitle() == null ? "" : lesson.getTitle().trim().toLowerCase();
        String videoUrl = lesson.getVideoUrl() == null ? "" : lesson.getVideoUrl().trim().toLowerCase();
        return title + "|" + videoUrl;
    }

    // Normaliza y persiste etiquetas recibidas en el formulario de curso.
    private Set<Tag> normalizeTags(List<String> tagNames) {
        var tagSet = new HashSet<Tag>();
        if (tagNames != null) {
            for (String tagName : tagNames) {
                if (tagName != null && !tagName.isBlank()) {
                    Tag tag = tagRepository.findByName(tagName.trim())
                            .orElseGet(() -> tagRepository.save(new Tag(tagName.trim())));
                    tagSet.add(tag);
                }
            }
        }
        return tagSet;
    }

    // Normaliza módulos y lecciones descartando elementos inválidos.
    private List<Module> normalizeModules(List<Module> sourceModules) {
        List<Module> normalizedModules = new ArrayList<>();
        if (sourceModules != null) {
            int moduleIndex = 0;
            for (Module sourceModule : sourceModules) {
                if (sourceModule == null || sourceModule.getTitle() == null || sourceModule.getTitle().isBlank()) {
                    continue;
                }

                Module normalizedModule = new Module();
                normalizedModule.setTitle(sourceModule.getTitle().trim());
                normalizedModule.setDescription(sourceModule.getDescription());
                normalizedModule.setOrderIndex(moduleIndex++);

                if (sourceModule.getLessons() != null) {
                    int lessonIndex = 0;
                    for (Lesson sourceLesson : sourceModule.getLessons()) {
                        if (sourceLesson == null || sourceLesson.getTitle() == null
                                || sourceLesson.getTitle().isBlank()) {
                            continue;
                        }

                        Lesson normalizedLesson = new Lesson();
                        normalizedLesson.setTitle(sourceLesson.getTitle().trim());
                        normalizedLesson.setVideoUrl(sourceLesson.getVideoUrl());
                        normalizedLesson.setOrderIndex(lessonIndex++);
                        normalizedModule.addLesson(normalizedLesson);
                    }
                }

                normalizedModules.add(normalizedModule);
            }
        }
        return normalizedModules;
    }

    // Convierte la media de valoración en una lista de estrellas llenas/vacías.
    public List<Boolean> getStarsFromAverage(Course course) {
        List<Boolean> stars = new ArrayList<>();
        double average = getAverageRating(course); // tu método actual que devuelve double
        int fullStars = (int) Math.round(average); // redondeamos al entero más cercano

        for (int i = 0; i < 5; i++) {
            stars.add(i < fullStars); // true = estrella llena, false = vacía
        }
        return stars;
    }

    // Obtiene iniciales para avatares a partir del nombre de usuario.
    private String getInitials(String username) {
        if (username == null || username.isBlank()) {
            return "";
        }
        String[] parts = username.trim().split("\\s+");
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(Character.toUpperCase(parts[i].charAt(0)));
            }
        }
        return initials.toString();
    }

    public Map<String, Long> getNumberGenre(Long courseId) {
        Map<String, Long> genreCount = new HashMap<>();
        genreCount.put("MALE", 0L);
        genreCount.put("FEMALE", 0L);

        List<Enrollment> enrollments = enrollmentRepository.findAll();
        for (Enrollment e : enrollments) {
            Course c = e.getCourse();
            if (c != null && (courseId == null || c.getId().equals(courseId))) {
                User u = e.getUser();
                if (u != null && u.getGender() != null) {
                    String g = u.getGender().toUpperCase();
                    if (g.equals("MALE") || g.equals("FEMALE")) {
                        genreCount.put(g, genreCount.getOrDefault(g, 0L) + 1);
                    }
                }
            }
        }
        return genreCount;
    }

    public List<Long> getAgesCourse(Long courseId) {
        long count18_25 = 0;
        long count26_35 = 0;
        long count36_50 = 0;
        long count50plus = 0;

        List<Enrollment> enrollments = enrollmentRepository.findAll();
        for (Enrollment e : enrollments) {
            Course c = e.getCourse();
            if (c != null && (courseId == null || c.getId().equals(courseId))) {
                User u = e.getUser();
                if (u != null && u.getBirthDate() != null) {
                    int age = java.time.Period.between(u.getBirthDate(), java.time.LocalDate.now()).getYears();
                    if (age >= 18 && age <= 25) {
                        count18_25++;
                    } else if (age > 25 && age <= 35) {
                        count26_35++;
                    } else if (age > 35 && age <= 50) {
                        count36_50++;
                    } else if (age > 50) {
                        count50plus++;
                    }
                }
            }
        }
        return java.util.Arrays.asList(count18_25, count26_35, count36_50, count50plus);
    }

    public List<Tag> getCommonTags(long userId) {
        Set<Tag> userTags = new HashSet<>();
        List<Enrollment> userEnrollments = enrollmentRepository.findByUserId(userId);
        for (Enrollment e : userEnrollments) {
            Course c = e.getCourse();
            if (c != null && c.getTags() != null) {
                userTags.addAll(c.getTags());
            }
        }

        Map<Tag, Set<Long>> tagUsers = new HashMap<>();
        for (Tag t : userTags) {
            tagUsers.put(t, new HashSet<>());
        }

        List<Enrollment> allEnrollments = enrollmentRepository.findAll();
        for (Enrollment e : allEnrollments) {
            Course c = e.getCourse();
            User u = e.getUser();
            if (c != null && c.getTags() != null && u != null) {
                for (Tag t : c.getTags()) {
                    if (tagUsers.containsKey(t)) {
                        if (!u.getId().equals(userId)) {
                            tagUsers.get(t).add(u.getId());
                        }
                    }
                }
            }
        }

        List<Tag> sortedTags = new ArrayList<>();
        for (Map.Entry<Tag, Set<Long>> entry : tagUsers.entrySet()) {
            if (entry.getValue().size() > 0) {
                sortedTags.add(entry.getKey());
            }
        }

        sortedTags.sort((t1, t2) -> {
            int count1 = tagUsers.get(t1).size();
            int count2 = tagUsers.get(t2).size();
            return Integer.compare(count2, count1);
        });

        return sortedTags.size() > 3 ? sortedTags.subList(0, 3) : sortedTags;
    }

    public long getTotalLessons(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null || course.getModules() == null) {
            return 0;
        }
        return course.getModules().stream()
                .filter(m -> m.getLessons() != null)
                .mapToLong(m -> m.getLessons().size())
                .sum();
    }
}