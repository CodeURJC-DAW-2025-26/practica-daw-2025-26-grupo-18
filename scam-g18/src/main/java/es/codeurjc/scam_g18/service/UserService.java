package es.codeurjc.scam_g18.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.Image;
import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.model.Subscription;
import es.codeurjc.scam_g18.model.SubscriptionStatus;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.SubscriptionRepository;
import es.codeurjc.scam_g18.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RolerService rolerService;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    public boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal());
    }

    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "";
    }

    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Optional<User> user;
            if (auth instanceof OAuth2AuthenticationToken) {
                user = userRepository.findByEmail(auth.getName());
            } else {
                user = userRepository.findByUsername(auth.getName());
            }
            if (user.isPresent()) {
                return user.get().getId();
            }
        }
        return null;
    }

    public Optional<User> getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            // Para login OAuth2 (Google), auth.getName() devuelve el email
            if (auth instanceof OAuth2AuthenticationToken) {
                return findByEmail(auth.getName());
            }
            // Para login tradicional, auth.getName() devuelve el username
            return findByUsername(auth.getName());
        }
        return Optional.empty();
    }

    @Transactional
    public String getCurrentUserProfileImage() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return getProfileImage(userId);
        }
        return "/img/default_avatar.png";
    }

    @Transactional
    public String getProfileImage(Long id) {
        Optional<User> user = findById(id);
        if (user.isPresent() && user.get().getImage() != null) {
            return imageService.getConnectionImage(user.get().getImage());
        }
        return "/img/default_avatar.png";
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public boolean updateProfile(Long id, String username, String email, String country,
            String shortDescription, String currentGoal, String weeklyRoutine,
            String comunity, MultipartFile imageFile) throws IOException, SQLException {
        Optional<User> optUser = userRepository.findById(id);
        if (optUser.isEmpty())
            return false;

        User user = optUser.get();
        if (username != null && !username.isBlank()) {
            user.setUsername(username);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        if (country != null && !country.isBlank()) {
            user.setCountry(country);
        }
        // Campos de texto libre — se permiten vacíos para borrar el valor
        user.setShortDescription(shortDescription);
        user.setCurrentGoal(currentGoal);
        user.setWeeklyRoutine(weeklyRoutine);
        user.setComunity(comunity);
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setImage(imageService.saveImage(imageFile));
        }
        return true;
    }

    public int getCompletedCoursesCount(Long userId) {
        return enrollmentRepository.countByUserIdAndProgressPercentage(userId, 100);
    }

    public String getUserType(Long userId) {
        Optional<User> optUser = findById(userId);
        if (optUser.isEmpty())
            return "Sin suscripción";

        User user = optUser.get();
        boolean isAdmin = user.getRoles().stream()
                .map(Role::getName)
                .anyMatch(name -> name.equalsIgnoreCase("ADMIN"));
        if (isAdmin) {
            return "Admin";
        } else if (subscriptionRepository.findByUserIdAndStatus(userId, SubscriptionStatus.ACTIVE).isPresent()) {
            return "Suscrito";
        }
        return "Sin suscripción";
    }

    public void updateName(String newName, User user) {
        user.setUsername(newName);
    }

    public void updateCountry(String country, User user) {
        user.setCountry(country);
    }

    public void updateImage(String imgPath, User user) throws IOException, SQLException {
        Image img = imageService.saveImage(imgPath);
        user.setImage(img);
    }

    public boolean isSuscribedToCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseIdAndExpiresAtAfter(userId, courseId, LocalDateTime.now());
    }

    @Transactional
    public void checkAndExpireSubscription(User user) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByUserIdAndStatus(user.getId(),
                SubscriptionStatus.ACTIVE);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDateTime.now())) {
                subscription.setStatus(SubscriptionStatus.EXPIRED);
                subscriptionRepository.save(subscription);

                user.getRoles().removeIf(role -> role.getName().equals("SUBSCRIBED"));
                userRepository.save(user);
            }
        }
    }

    @Transactional
    public boolean registerUser(String username, String email, String rawPassword, String gender, String birthDate,
            String country, MultipartFile imageFile) throws IOException, SQLException {
        if (findByUsername(username).isPresent()) {
            return false;
        }
        if (userRepository.findByEmail(email).isPresent()) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(rawPassword));
        newUser.setGender(gender);
        newUser.setBirthDate(LocalDate.parse(birthDate));
        newUser.setCountry(country);

        if (imageFile != null && !imageFile.isEmpty()) {
            newUser.setImage(imageService.saveImage(imageFile));
        }

        Role userRole = rolerService.findByName("USER").orElseGet(() -> {
            Role newRole = new Role("USER");
            rolerService.save(newRole);
            return newRole;
        });

        newUser.getRoles().add(userRole);
        save(newUser);
        return true;
    }
}