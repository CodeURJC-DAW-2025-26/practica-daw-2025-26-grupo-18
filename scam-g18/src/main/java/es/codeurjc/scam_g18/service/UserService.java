package es.codeurjc.scam_g18.service;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.model.User;
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
            String username = auth.getName();
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                return user.get().getId();
            }
        }
        return null;
    }

    public Optional<User> getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
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
        return "/img/descarga.jpg";
    }

    @Transactional
    public String getProfileImage(Long id) {
        Optional<User> user = findById(id);
        if (user.isPresent() && user.get().getImage() != null) {
            return imageService.getConnectionImage(user.get().getImage());
        }
        return "/img/descarga.jpg";
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public boolean registerUser(String username, String email, String rawPassword, String gender, String birthDate,
            String country, MultipartFile imageFile) throws IOException, SQLException {
        if (findByUsername(username).isPresent()) {
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