package es.codeurjc.scam_g18.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.codeurjc.scam_g18.repository.UserRepository;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.model.Image;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Más adelante aquí inyectarás UserRepository, HttpSession, etc.

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

    public String getCurrentUserProfileImage() {
        String username = getCurrentUserName();
        if (!username.isEmpty()) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                Image img = user.get().getImage();
                if (img != null) {
                    return img.getUrl();
                }
            }
        }
        return "/img/descarga.jpg"; // Default image
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}