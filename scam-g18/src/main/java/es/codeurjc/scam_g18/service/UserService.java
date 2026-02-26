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

    // Indica si hay un usuario autenticado en la sesión actual.
    public boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() &&
                !"anonymousUser".equals(auth.getPrincipal());
    }

    // Devuelve el nombre del usuario autenticado actual.
    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return "";
    }

    // Devuelve el id del usuario autenticado actual.
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

    // Devuelve la entidad User del usuario autenticado actual.
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
    // Obtiene la imagen de perfil del usuario actual.
    public String getCurrentUserProfileImage() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return getProfileImage(userId);
        }
        return "/img/default_avatar.png";
    }

    @Transactional
    // Obtiene la URL de imagen de perfil de un usuario por id.
    public String getProfileImage(Long id) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            return "/images/users/" + id + "/profile";
        }
        return "/img/default_avatar.png";
    }

    // Busca un usuario por username.
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Busca un usuario por email.
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Busca un usuario por id.
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Comprueba si ya existe un usuario con ese username (sin distinguir mayúsculas).
    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    // Comprueba si ya existe un usuario con ese email (sin distinguir mayúsculas).
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    // Guarda los cambios de un usuario.
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    // Actualiza los datos editables del perfil de usuario.
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

    // Cuenta cursos completados por un usuario.
    public int getCompletedCoursesCount(Long userId) {
        return enrollmentRepository.countByUserIdAndProgressPercentage(userId, 100);
    }

    // Devuelve el tipo de cuenta visible del usuario (Admin/Suscrito/Sin
    // suscripción).
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

    // Actualiza el nombre de usuario en memoria.
    public void updateName(String newName, User user) {
        user.setUsername(newName);
    }

    // Actualiza el país de un usuario en memoria.
    public void updateCountry(String country, User user) {
        user.setCountry(country);
    }

    // Actualiza la imagen de usuario desde una ruta local.
    public void updateImage(String imgPath, User user) throws IOException, SQLException {
        Image img = imageService.saveImage(imgPath);
        user.setImage(img);
    }

    // Comprueba si un usuario tiene suscripción activa a un curso.
    public boolean isSuscribedToCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseIdAndExpiresAtAfter(userId, courseId, LocalDateTime.now());
    }

    @Transactional
    // Expira una suscripción vencida y retira su rol asociado.
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

    // Valida que los datos provistos para registro/edición cumplan formatos lógicos
    // y maneja restricciones de nulos/vacíos centrales.
    public String validateUserAttributes(String username, String email, String password, String birthDateStr,
            String gender, String country) {

        StringBuilder errors = new StringBuilder();

        // 1. Mandatory Fields Presence Check
        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            return "El nombre de usuario y el correo electrónico son obligatorios.";
        }

        // Si se proveen el resto de campos (Registro normal), exigimos que no estén
        // vacíos
        if (password != null && password.isBlank()) {
            return "Por favor, introduzca una contraseña.";
        }
        if (birthDateStr != null && birthDateStr.isBlank()) {
            return "Por favor, introduzca una fecha de nacimiento.";
        }
        if (gender != null && gender.isBlank()) {
            return "Por favor, seleccione un género.";
        }
        if (country != null && country.isBlank()) {
            return "Por favor, seleccione un país.";
        }

        // 2. Format Checks
        if (username != null) {
            if (username.length() < 3 || username.length() > 20) {
                errors.append("El nombre de usuario debe tener entre 3 y 20 caracteres.<br>");
            }
        }
        if (password != null) {
            String passwordPattern = "^(?=.*[0-9])(?=.*[A-Z])(?=\\S+$).{8,}$";
            if (!password.matches(passwordPattern)) {
                errors.append(
                        "La contraseña debe tener al menos 8 caracteres, incluir un número y una letra mayúscula.<br>");
            }
        }
        if (birthDateStr != null) {
            try {
                LocalDate birthDate = LocalDate.parse(birthDateStr);
                if (birthDate.isAfter(LocalDate.now().minusYears(18))) {
                    errors.append("Debes tener al menos 18 años para registrarte.<br>");
                }
            } catch (Exception e) {
                errors.append("La fecha de nacimiento no tiene un formato válido.<br>");
            }
        }

        return errors.length() > 0 ? errors.toString() : null;
    }

    @Transactional
    // Registra un nuevo usuario si username y email están disponibles.
    public boolean registerUser(String username, String email, String rawPassword, String gender, String birthDate,
            String country, MultipartFile imageFile) throws IOException, SQLException {
        String normalizedUsername = username != null ? username.trim() : "";
        String normalizedEmail = email != null ? email.trim() : "";

        if (usernameExists(normalizedUsername)) {
            return false;
        }
        if (emailExists(normalizedEmail)) {
            return false;
        }

        User newUser = new User();
        newUser.setUsername(normalizedUsername);
        newUser.setEmail(normalizedEmail);
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