package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.ReviewRepository;
import es.codeurjc.scam_g18.repository.UserRepository;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private EventRepository eventRepository;

    // ---- Users ----

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void banUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);
        }
    }

    public void unbanUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(true);
            userRepository.save(user);
        }
    }

    // ---- Courses ----

    public List<Course> getAllCoursesSortedByStatus() {
        List<Course> pending = courseRepository.findByStatus(Status.PENDING_REVIEW);
        List<Course> others = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    public List<Course> searchCourses(String query) {
        List<Course> found = courseRepository.findByTitleContainingIgnoreCase(query);
        // pending first
        List<Course> pending = found.stream().filter(c -> c.getStatus() == Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> others = found.stream().filter(c -> c.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(Status.PENDING_REVIEW);
    }

    public void approveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.PUBLISHED);
            courseRepository.save(course);
        }
    }

    public void rejectCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.DRAFT);
            courseRepository.save(course);
        }
    }

    public void archiveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.ARCHIVED);
            courseRepository.save(course);
        }
    }

    // ---- Events ----

    public List<Event> getAllEventsSortedByStatus() {
        List<Event> pending = eventRepository.findByStatus(Status.PENDING_REVIEW);
        List<Event> others = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    public List<Event> searchEvents(String query) {
        List<Event> found = eventRepository.findByTitleContainingIgnoreCase(query);
        List<Event> pending = found.stream().filter(e -> e.getStatus() == Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> others = found.stream().filter(e -> e.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    public void approveEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.PUBLISHED);
            eventRepository.save(event);
        }
    }

    public void rejectEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.DRAFT);
            eventRepository.save(event);
        }
    }

    // ---- Reviews ----

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
