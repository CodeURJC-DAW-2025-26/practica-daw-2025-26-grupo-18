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

    // Obtiene todos los usuarios registrados.
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Busca usuarios por coincidencia parcial de username.
    public List<User> searchUsers(String query) {
        return userRepository.findByUsernameContainingIgnoreCase(query);
    }

    // Busca un usuario por id.
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Busca un usuario por nombre de usuario exacto.
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Desactiva un usuario y le envía notificación por correo.
    public void banUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(false);
            userRepository.save(user);
            // Enviar email de notificación al usuario baneado
            emailService.accountBannedMessage(user.getEmail(), user.getUsername());
        }
    }

    // Reactiva una cuenta de usuario bloqueada.
    public void unbanUser(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsActive(true);
            userRepository.save(user);
        }
    }

    // ---- Courses ----

    // Obtiene cursos priorizando los pendientes de revisión.
    public List<Course> getAllCoursesSortedByStatus() {
        List<Course> pending = courseRepository.findByStatus(Status.PENDING_REVIEW);
        List<Course> others = courseRepository.findAll().stream()
                .filter(c -> c.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Course> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    // Busca cursos por título priorizando los pendientes.
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

    // Obtiene cursos pendientes de revisión.
    public List<Course> getPendingCourses() {
        return courseRepository.findByStatus(Status.PENDING_REVIEW);
    }

    // Publica un curso pendiente y notifica a su creador.
    public void approveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.PUBLISHED);
            courseRepository.save(course);
            // Notificar al creador del curso
            User creator = course.getCreator();
            if (creator != null) {
                emailService.cursePublished(creator.getEmail(), course.getTitle(), creator.getUsername());
            }
        }
    }

    // Rechaza un curso devolviéndolo a borrador.
    public void rejectCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.DRAFT);
            courseRepository.save(course);
        }
    }

    // Archiva un curso.
    public void archiveCourse(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isPresent()) {
            Course course = courseOptional.get();
            course.setStatus(Status.ARCHIVED);
            courseRepository.save(course);
        }
    }

    // ---- Events ----

    // Obtiene eventos priorizando los pendientes de revisión.
    public List<Event> getAllEventsSortedByStatus() {
        List<Event> pending = eventRepository.findByStatus(Status.PENDING_REVIEW);
        List<Event> others = eventRepository.findAll().stream()
                .filter(e -> e.getStatus() != Status.PENDING_REVIEW)
                .collect(Collectors.toList());
        List<Event> result = new ArrayList<>(pending);
        result.addAll(others);
        return result;
    }

    // Busca eventos por título priorizando los pendientes.
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

    // Publica un evento pendiente y notifica al creador.
    public void approveEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.PUBLISHED);
            eventRepository.save(event);
            // Notificar al creador del evento
            User creator = event.getCreator();
            if (creator != null) {
                emailService.eventPublished(creator.getEmail(), event.getTitle(), creator.getUsername());
            }
        }
    }

    // Rechaza un evento devolviéndolo a borrador.
    public void rejectEvent(Long eventId) {
        Optional<Event> opt = eventRepository.findById(eventId);
        if (opt.isPresent()) {
            Event event = opt.get();
            event.setStatus(Status.DRAFT);
            eventRepository.save(event);
        }
    }

    // ---- Reviews ----

    // Obtiene todas las reseñas del sistema.
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // ---- Orders ----

    // Obtiene todos los pedidos ordenados por fecha de creación descendente.
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

    // Elimina una reseña por su id.
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}
