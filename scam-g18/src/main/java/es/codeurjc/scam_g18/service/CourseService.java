package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Lesson;
import es.codeurjc.scam_g18.model.Module;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ImageService imageService;

    public List<Course> getFeaturedCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

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

    public Course getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public List<Map<String, Object>> getCoursesViewData(String keyword, List<String> tags) {
        List<Course> allCourses = searchCourses(keyword, tags);
        List<Map<String, Object>> enrichedCourses = new ArrayList<>();

        for (Course course : allCourses) {
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

    public Map<String, Object> getCourseDetailViewData(Long id) {
        Course course;
        try {
            course = getCourseById(id);
        } catch (RuntimeException e) {
            return null;
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
        courseData.put("videoHours", course.getVideoHours() );
        courseData.put("downloadableResources", course.getDownloadableResources());

        Map<String, Object> creatorData = new HashMap<>();
        String creatorUsername = "Desconocido";
        if (course.getCreator() != null && course.getCreator().getUsername() != null) {
            creatorUsername = course.getCreator().getUsername();
        }
        creatorData.put("username", creatorUsername);
        courseData.put("creator", creatorData);

        Map<String, Object> imageData = new HashMap<>();
        String courseImageUrl = "/img/descarga.jpg";
        if (course.getImage() != null) {
            courseImageUrl = imageService.getConnectionImage(course.getImage());
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
                lessonData.put("title", lesson.getTitle());
                lessonData.put("videoUrl", lesson.getVideoUrl());
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
            userData.put("initials", user != null ? user.getInitials() : getInitials(username));

            if (user != null && user.getImage() != null) {
                Map<String, Object> userImageData = new HashMap<>();
                userImageData.put("url", imageService.getConnectionImage(user.getImage()));
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

        return detailData;
    }

    public Double getAverageRating(Course course) {
        if (course.getReviews() == null || course.getReviews().isEmpty())
            return 0.0;
        return course.getReviews().stream()
                .filter(r -> r.getRating() != null)
                .mapToInt(r -> r.getRating())
                .average()
                .orElse(0.0);
    }

    public Integer getRatingCount(Course course) {
        if (course.getReviews() == null)
            return 0;
        return (int) course.getReviews().stream()
                .filter(r -> r.getRating() != null)
                .count();
    }

    public String getPriceInEuros(Course course) {
        if (course.getPriceCents() == null)
            return "0.00";
        return String.format("%.2f", course.getPriceCents() / 100.0);
    }

    public int getReviewsNumber(Course course) {
        if (course.getReviews() == null)
            return 0;
        return course.getReviews().size();
    }

    public void incrementSubscribers(Course course) {
        course.setSubscribersNumber(course.getSubscribersNumber() + 1);
        courseRepository.save(course);
    }

    public List<Boolean> getStarsFromAverage(Course course) {
        List<Boolean> stars = new ArrayList<>();
        double average = getAverageRating(course); // tu método actual que devuelve double
        int fullStars = (int) Math.round(average); // redondeamos al entero más cercano

        for (int i = 0; i < 5; i++) {
            stars.add(i < fullStars); // true = estrella llena, false = vacía
        }
        return stars;
    }

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
}