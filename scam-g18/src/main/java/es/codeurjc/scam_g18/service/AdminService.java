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

    @Autowired
    private EmailService emailService;

    // ---- Users ----

    // Retrieves all registered users.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllUsers(int page, int size) {
        return paginate(userRepository.findAll(), page, size);
    }

    public int getTotalUsersCount() {
        return (int) userRepository.count();
    }

    // Searches users by partial username match.
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    public List<User> searchUsers(String query, int page, int size) {
        return paginate(userRepository.findByUsernameContainingIgnoreCase(query), page, size);
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
        return sortPendingFirst(pending, others);
    }

    // Searches courses by title while prioritizing pending ones.
    public List<Course> searchCourses(String query) {
        List<Course> found = courseRepository.findByTitleContainingIgnoreCase(query);
        List<Course> pending = found.stream().filter(c -> c.getStatus() == Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> others = found.stream().filter(c -> c.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        return sortPendingFirst(pending, others);
    }

    public List<Course> getAllCoursesSortedByStatus(int page, int size) {
        return paginate(getAllCoursesSortedByStatus(), page, size);
    }

    public int getTotalCoursesCount() {
        return (int) courseRepository.count();
    }

    public List<Course> searchCourses(String query, int page, int size) {
        return paginate(searchCourses(query), page, size);
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
        return sortPendingFirst(pending, others);
    }

    public List<Event> getAllEventsSortedByStatus(int page, int size) {
        return paginate(getAllEventsSortedByStatus(), page, size);
    }

    public int getTotalEventsCount() {
        return (int) eventRepository.count();
    }

    // Searches events by title while prioritizing pending ones.
    public List<Event> searchEvents(String query) {
        List<Event> found = eventRepository.findByTitleContainingIgnoreCase(query);
        List<Event> pending = found.stream().filter(e -> e.getStatus() == Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> others = found.stream().filter(e -> e.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        return sortPendingFirst(pending, others);
    }

    public List<Event> searchEvents(String query, int page, int size) {
        return paginate(searchEvents(query), page, size);
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
        return paginate(getAllOrdersSortedByDate(), page, size);
    }

    public int getTotalOrdersCount() {
        return (int) orderRepository.count();
    }

    // Deletes a review by id.
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    // ---- API DTO mappers (used by AdminController AJAX endpoints) ----

    // Maps a page of users to a serializable list for the admin AJAX endpoint.
    public List<java.util.Map<String, Object>> getUsersApiData(String query, int page, int size) {
        List<User> users = (query != null && !query.isBlank())
                ? searchUsers(query, page, size)
                : getAllUsers(page, size);

        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("isActive", u.getIsActive());
            map.put("isSubscribed", u.getIsSubscribed());
            result.add(map);
        }
        return result;
    }

    // Maps a page of courses to a serializable list for the admin AJAX endpoint.
    public List<java.util.Map<String, Object>> getCoursesApiData(String query, int page, int size) {
        List<Course> courses = (query != null && !query.isBlank())
                ? searchCourses(query, page, size)
                : getAllCoursesSortedByStatus(page, size);

        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (Course c : courses) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            map.put("title", c.getTitle());
            map.put("shortDescription", c.getShortDescription());
            map.put("isPendingReview", c.getStatus() == Status.PENDING_REVIEW);
            map.put("status", statusToString(c.getStatus()));
            map.put("creatorUsername", c.getCreator() != null ? c.getCreator().getUsername() : "");
            result.add(map);
        }
        return result;
    }

    // Maps a page of events to a serializable list for the admin AJAX endpoint.
    public List<java.util.Map<String, Object>> getEventsApiData(String query, int page, int size) {
        List<Event> events = (query != null && !query.isBlank())
                ? searchEvents(query, page, size)
                : getAllEventsSortedByStatus(page, size);

        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (Event e : events) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("category", e.getCategory());
            map.put("isPendingReview", e.getStatus() == Status.PENDING_REVIEW);
            map.put("status", statusToString(e.getStatus()));
            map.put("creatorUsername", e.getCreator() != null ? e.getCreator().getUsername() : "");
            result.add(map);
        }
        return result;
    }

    // Maps a page of orders to a serializable list for the admin AJAX endpoint.
    public List<java.util.Map<String, Object>> getOrdersApiData(int page, int size) {
        List<Order> orders = getAllOrdersSortedByDate(page, size);
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (Order o : orders) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", o.getId());
            if (o.getUser() != null)
                map.put("username", o.getUser().getUsername());
            map.put("billingFullName", o.getBillingFullName());
            map.put("billingEmail", o.getBillingEmail());
            map.put("status", statusToString(o.getStatus()));
            if (o.getPaidAt() != null) {
                map.put("paidAt", o.getPaidAt().format(formatter));
            } else if (o.getCreatedAt() != null) {
                map.put("createdAt", o.getCreatedAt().format(formatter));
            }
            map.put("paymentMethod", o.getPaymentMethod());
            map.put("paymentReference", o.getPaymentReference());
            map.put("totalAmountEuros", o.getTotalAmountEuros());
            result.add(map);
        }
        return result;
    }

    // ---- Private helpers ----

    // Converts an enum status to its string representation, or empty string if
    // null.
    private String statusToString(Enum<?> status) {
        return status != null ? status.toString() : "";
    }

    // Returns a sublist of the given list for the requested page and size.
    private <T> List<T> paginate(List<T> list, int page, int size) {
        int start = page * size;
        if (start >= list.size())
            return new ArrayList<>();
        return list.subList(start, Math.min(start + size, list.size()));
    }

    // Concatenates two partial lists putting pending-review items first.
    private <T> List<T> sortPendingFirst(List<T> pending, List<T> others) {
        List<T> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }
}
