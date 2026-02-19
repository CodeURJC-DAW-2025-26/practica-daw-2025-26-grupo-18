package es.codeurjc.scam_g18.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserRepository userRepository;

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
}