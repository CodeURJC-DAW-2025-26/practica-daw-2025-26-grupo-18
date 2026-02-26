package es.codeurjc.scam_g18.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.ReviewRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // Crea una reseña si el usuario aún no ha valorado ese curso.
    public boolean addReview(User user, Course course, int rating, String content) {
        if (reviewRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            return false;
        }
        Review newReview = new Review(user, course, content, rating);
        reviewRepository.save(newReview);
        return true;
    }

    // Comprueba si un usuario ya ha publicado una reseña para un curso.
    public boolean hasUserReviewed(Long userId, Long courseId) {
        return reviewRepository.existsByUserIdAndCourseId(userId, courseId);
    }
}
