package es.codeurjc.scam_g18.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
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
}