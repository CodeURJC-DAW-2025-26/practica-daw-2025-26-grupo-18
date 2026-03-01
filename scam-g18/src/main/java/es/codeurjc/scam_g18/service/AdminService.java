package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.OrderRepository;
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

    @Autowired
    private OrderRepository orderRepository;

    // ---- Users ----

    @Autowired
    private EmailService emailService;

    // Retrieves all registered users.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsers(int page, int size) {
        List<User> users = userRepository.findAll();
        int start = page * size;
        int end = Math.min((start + size), users.size());
        if (start >= users.size()) {
            return new ArrayList<>();
        }
        return users.subList(start, end);
    }

    public int getTotalUsersCount() {
        return userRepository.findAll().size();
    }

    // Searches users by partial username match.
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public List<User> searchUsers(String query, int page, int size) {
        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        int start = page * size;
        int end = Math.min((start + size), users.size());
        if (start >= users.size()) {
            return new ArrayList<>();
        }
        return users.subList(start, end);
    }

    public int getTotalSearchUsersCount(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query).size();
    }

    // Looks up a user by id.
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Looks up a user by exact username.
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Deactivates a user and sends them an email notification.
    public void banUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);
            // Send notification email to the banned user
            emailService.accountBannedMessage(user.getEmail(), user.getUsername());
        }
    }

    // Reactivates a blocked user account.
    public void unbanUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(true);
            userRepository.save(user);
        }
    }

    // ---- Courses ----

    // Retrieves courses prioritizing those pending review.
    public List<Course> getAllCoursesSortedByStatus() {
        List<Course> pending = courseRepository.findByStatus(Status.PENDING_REVIEW);
        List<Course> others = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    // Searches courses by title while prioritizing pending ones.
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

    public List<Course> getAllCoursesSortedByStatus(int page, int size) {
        List<Course> result = getAllCoursesSortedByStatus();
        int start = page * size;
        int end = Math.min((start + size), result.size());
        if (start >= result.size()) {
            return new ArrayList<>();
        }
        return result.subList(start, end);
    }

    public int getTotalCoursesCount() {
        return courseRepository.findAll().size();
    }

    public List<Course> searchCourses(String query, int page, int size) {
        List<Course> result = searchCourses(query);
        int start = page * size;
        int end = Math.min((start + size), result.size());
        if (start >= result.size()) {
            return new ArrayList<>();
        }
        return result.subList(start, end);
    }

    public int getTotalSearchCoursesCount(String query) {
        return courseRepository.findByTitleContainingIgnoreCase(query).size();
    }

    // Retrieves courses pending review.
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(Status.PENDING_REVIEW);
    }

    // Publishes a pending course and notifies its creator.
    public void approveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.PUBLISHED);
            courseRepository.save(course);
            // Notify the course creator
            User creator = course.getCreator();
            if (creator != null) {
                emailService.cursePublished(creator.getEmail(), course.getTitle(), creator.getUsername());
            }
        }
    }

    // Rejects a course and moves it back to draft.
    public void rejectCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.DRAFT);
            courseRepository.save(course);
        }
    }

    // Archives a course.
    public void archiveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.ARCHIVED);
            courseRepository.save(course);
        }
    }

    // ---- Events ----

    // Retrieves events prioritizing those pending review.
    public List<Event> getAllEventsSortedByStatus() {
        List<Event> pending = eventRepository.findByStatus(Status.PENDING_REVIEW);
        List<Event> others = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    public List<Event> getAllEventsSortedByStatus(int page, int size) {
        List<Event> result = getAllEventsSortedByStatus();
        int start = page * size;
        int end = Math.min((start + size), result.size());
        if (start >= result.size()) {
            return new ArrayList<>();
        }
        return result.subList(start, end);
    }

    public int getTotalEventsCount() {
        return eventRepository.findAll().size();
    }

    // Searches events by title while prioritizing pending ones.
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

    public List<Event> searchEvents(String query, int page, int size) {
        List<Event> result = searchEvents(query);
        int start = page * size;
        int end = Math.min((start + size), result.size());
        if (start >= result.size()) {
            return new ArrayList<>();
        }
        return result.subList(start, end);
    }

    public int getTotalSearchEventsCount(String query) {
        return eventRepository.findByTitleContainingIgnoreCase(query).size();
    }

    // Publishes a pending event and notifies the creator.
    public void approveEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.PUBLISHED);
            eventRepository.save(event);
            // Notify the event creator
            User creator = event.getCreator();
            if (creator != null) {
                emailService.eventPublished(creator.getEmail(), event.getTitle(), creator.getUsername());
            }
        }
    }

    // Rejects an event and moves it back to draft.
    public void rejectEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.DRAFT);
            eventRepository.save(event);
        }
    }

    // ---- Reviews ----

    // Retrieves all reviews in the system.
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ---- Orders ----

    // Retrieves all orders sorted by creation date descending.
    public List<Order> getAllOrdersSortedByDate() {
        return orderRepository.findAll().stream()
                .sorted((o1, o2) -> {
                    if (o1.getCreatedAt() == null && o2.getCreatedAt() == null) {
                        return 0;
                    }
                    if (o1.getCreatedAt() == null) {
                        return 1;
                    }
                    if (o2.getCreatedAt() == null) {
                        return -1;
                    }
                    return o2.getCreatedAt().compareTo(o1.getCreatedAt());
                })
                .collect(Collectors.toList());
    }

    public List<Order> getAllOrdersSortedByDate(int page, int size) {
        List<Order> result = getAllOrdersSortedByDate();
        int start = page * size;
        int end = Math.min((start + size), result.size());
        if (start >= result.size()) {
            return new ArrayList<>();
        }
        return result.subList(start, end);
    }

    public int getTotalOrdersCount() {
        return orderRepository.findAll().size();
    }

    // Deletes a review by id.
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
