package es.codeurjc.scam_g18.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
    private es.codeurjc.scam_g18.security.RepositoryUserDetailsService userDetailsService;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    // Builds global header/view attributes for all pages.
    public Map<String, Object> getGlobalHeaderViewData(boolean hasPrincipal) {
        Map<String, Object> model = new HashMap<>();

        model.put("isUserLoggedIn", false);
        model.put("userId", "");
        model.put("userName", "");
        model.put("userProfileImage", "/img/default_avatar.png");
        model.put("canCreateEvent", false);
        model.put("canCreateCourse", false);
        model.put("isAdmin", false);
        model.put("isPublisher", false);

        if (!hasPrincipal) {
            return model;
        }

        var currentUser = getCurrentAuthenticatedUser().orElse(null);
        if (currentUser == null) {
            return model;
        }

        checkAndExpireSubscription(currentUser);

        boolean canCreate = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("SUBSCRIBED"));
        boolean isAdmin = currentUser.getRoles().stream()
                .anyMatch(role -> role.getName().equals("ADMIN"));

        model.put("isUserLoggedIn", true);
        model.put("userId", currentUser.getId());
        model.put("userName", currentUser.getUsername());
        model.put("userProfileImage", getProfileImage(currentUser.getId()));
        model.put("canCreateEvent", canCreate);
        model.put("canCreateCourse", canCreate);
        model.put("isAdmin", isAdmin);
        model.put("isPublisher", canCreate);

        return model;
    }

    // Indicates whether there is an authenticated user in the current session.
    public boolean isUserLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return isActiveAuthentication(auth);
    }

    // Returns the current authenticated user's name.
    public String getCurrentUserName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isActiveAuthentication(auth)) {
            return auth.getName();
        }
        return "";
    }

    // Returns the current authenticated user's id.
    public Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isActiveAuthentication(auth)) {
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

    // Returns the User entity of the current authenticated user.
    public Optional<User> getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isActiveAuthentication(auth)) {
            // For OAuth2 login (Google), auth.getName() returns the email
            if (auth instanceof OAuth2AuthenticationToken) {
                return findByEmail(auth.getName());
            }
            // For traditional login, auth.getName() returns the username
            return findByUsername(auth.getName());
        }
        return Optional.empty();
    }

    // Returns true when the Authentication represents a real, non-anonymous user.
    private boolean isActiveAuthentication(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    @Transactional
    // Retrieves the current user's profile image.
    public String getCurrentUserProfileImage() {
        Long userId = getCurrentUserId();
        if (userId != null) {
            return getProfileImage(userId);
        }
        return "/img/default_avatar.png";
    }

    @Transactional
    // Retrieves profile image URL for a user by id.
    public String getProfileImage(Long id) {
        Optional<User> user = findById(id);
        if (user.isPresent()) {
            return "/images/users/" + id + "/profile";
        }
        return "/img/default_avatar.png";
    }

    // Finds a user by username.
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    // Finds a user by email.
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Finds a user by id.
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Checks whether a user with that username already exists (case-insensitive).
    public boolean usernameExists(String username) {
        if (username == null || username.isBlank()) {
            return false;
        }
        return userRepository.existsByUsernameIgnoreCase(username.trim());
    }

    // Checks whether a user with that email already exists (case-insensitive).
    public boolean emailExists(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return userRepository.existsByEmailIgnoreCase(email.trim());
    }

    // Saves user changes.
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    // Updates editable user profile data.
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
        // Free-text fields — empty values are allowed to clear the value
        user.setShortDescription(shortDescription);
        user.setCurrentGoal(currentGoal);
        user.setWeeklyRoutine(weeklyRoutine);
        user.setComunity(comunity);
        if (imageFile != null && !imageFile.isEmpty()) {
            user.setImage(imageService.saveImage(imageFile));
        }
        return true;
    }

    // Counts courses completed by a user.
    public int getCompletedCoursesCount(Long userId) {
        return enrollmentRepository.countByUserIdAndProgressPercentage(userId, 100);
    }

    // Returns the user's visible account type (Admin/Subscribed/No subscription).
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

    // Returns the list of all countries, formatted and alphabetically sorted.
    public java.util.List<String> getAllCountries() {
        String[] countryCodes = java.util.Locale.getISOCountries();
        java.util.List<String> paises = new java.util.ArrayList<>();
        for (String code : countryCodes) {
            java.util.Locale locale = new java.util.Locale.Builder().setLanguage("es").setRegion(code).build();
            paises.add(locale.getDisplayCountry());
        }
        java.util.Collections.sort(paises);
        return paises;
    }

    // Updates the username in memory.
    public void updateName(String newName, User user) {
        user.setUsername(newName);
    }

    // Updates a user's country in memory.
    public void updateCountry(String country, User user) {
        user.setCountry(country);
    }

    // Updates user image from a local path.
    public void updateImage(String imgPath, User user) throws IOException, SQLException {
        Image img = imageService.saveImage(imgPath);
        user.setImage(img);
    }

    // Checks whether a user has an active subscription to a course.
    public boolean isSuscribedToCourse(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseIdAndExpiresAtAfter(userId, courseId, LocalDateTime.now());
    }

    @Transactional
    // Expires an outdated subscription and removes its associated role.
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

    // Validates that registration/edit data follows expected formats
    // and handles core null/empty constraints.
    public String validateUserAttributes(String username, String email, String password, String birthDateStr,
            String gender, String country) {

        StringBuilder errors = new StringBuilder();

        // 1. Mandatory Fields Presence Check
        if (username == null || username.isBlank() || email == null || email.isBlank()) {
            return "El nombre de usuario y el correo electrónico son obligatorios.";
        }

        // If remaining fields are provided (normal registration), require non-empty
        // values
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
    // Registers a new user if username and email are available.
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

    // Refreshes the current user's Spring Security session, useful after profile
    // or role changes (e.g., subscription purchase).
    public void refreshUserSession(String newUsername, jakarta.servlet.http.HttpServletRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            String targetUsername = (newUsername != null && !newUsername.isBlank()) ? newUsername : auth.getName();

            try {
                org.springframework.security.core.userdetails.UserDetails updatedUserDetails = userDetailsService
                        .loadUserByUsername(targetUsername);
                org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        updatedUserDetails,
                        updatedUserDetails.getPassword(),
                        updatedUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(newAuth);

                jakarta.servlet.http.HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
                }
            } catch (Exception e) {
                System.err.println("Error refrescando sesión de usuario: " + e.getMessage());
            }
        }
    }

    // Derives a username suggestion from Google OAuth2 attributes.
    public String buildSuggestedUsername(org.springframework.security.oauth2.core.user.OAuth2User oAuth2User,
            String email) {
        String suggested = oAuth2User.getAttribute("given_name");
        if (suggested == null || suggested.isBlank()) {
            suggested = oAuth2User.getAttribute("name");
        }
        if ((suggested == null || suggested.isBlank()) && email != null && email.contains("@")) {
            suggested = email.substring(0, email.indexOf('@'));
        }
        if (suggested == null)
            return "";
        return suggested.trim().replaceAll("\\s+", "");
    }

    // Derives a country suggestion from the Google locale attribute.
    public String buildSuggestedCountry(org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
        String localeAttribute = oAuth2User.getAttribute("locale");
        if (localeAttribute == null || localeAttribute.isBlank())
            return "";
        java.util.Locale googleLocale = java.util.Locale.forLanguageTag(localeAttribute.replace('_', '-'));
        if (googleLocale.getCountry() == null || googleLocale.getCountry().isBlank())
            return "";
        return googleLocale.getDisplayCountry(java.util.Locale.of("es", "ES"));
    }

    // Builds an OAuth2 authentication token for a freshly registered user and
    // stores it in the SecurityContext and HTTP session — moved from
    // RegisterGoogleController.
    public void authenticateOAuth2User(User newUser,
            org.springframework.security.oauth2.core.user.OAuth2User oAuth2User,
            jakarta.servlet.http.HttpServletRequest request) {

        java.util.List<org.springframework.security.core.GrantedAuthority> authorities = newUser.getRoles().stream()
                .map(role -> (org.springframework.security.core.GrantedAuthority) new org.springframework.security.core.authority.SimpleGrantedAuthority(
                        "ROLE_" + role.getName()))
                .collect(java.util.stream.Collectors.toList());

        org.springframework.security.oauth2.core.user.DefaultOAuth2User authenticatedUser = new org.springframework.security.oauth2.core.user.DefaultOAuth2User(
                authorities, oAuth2User.getAttributes(), "email");

        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(authenticatedUser, authorities, "google");

        org.springframework.security.core.context.SecurityContext securityContext = SecurityContextHolder
                .createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        jakarta.servlet.http.HttpSession session = request.getSession(true);
        session.setAttribute(
                org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);
    }
}
