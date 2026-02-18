package es.codeurjc.scam_g18.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.scam_g18.repository.UserRepository;
import es.codeurjc.scam_g18.model.User;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Más adelante aquí inyectarás UserRepository, HttpSession, etc.

    public boolean isUserLoggedIn() {
        // Aquí va tu lógica: verificar si hay usuario en sesión/auth
        return false; // Por ahora
    }

    public String getCurrentUserName() {
        // Obtener el nombre del usuario logueado
        return ""; // Por ahora vacío
    }

    public String getCurrentUserProfileImage() {
        // Obtener la imagen de perfil del usuario
        return ""; // Por ahora valor por defecto
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}