package es.codeurjc.scam_g18.service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getFeaturedCourses() {
        return courseRepository.findTopRated(PageRequest.of(0, 6));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
    
    public Double getAverageRating(Course course) {
        if (course.getReviews() == null || course.getReviews().isEmpty()) return 0.0;
        return course.getReviews().stream()
            .filter(r -> r.getRating() != null)
            .mapToInt(r -> r.getRating())
            .average()
            .orElse(0.0);
    }
    
    public Integer getRatingCount(Course course) {
        if (course.getReviews() == null) return 0;
        return (int) course.getReviews().stream()
            .filter(r -> r.getRating() != null)
            .count();
    }
    
    public String getPriceInEuros(Course course) {
        if (course.getPriceCents() == null) return "0.00";
        return String.format("%.2f", course.getPriceCents() / 100.0);
    }
}