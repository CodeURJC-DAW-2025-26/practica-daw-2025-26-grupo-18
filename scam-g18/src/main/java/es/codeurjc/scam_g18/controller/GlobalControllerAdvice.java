package es.codeurjc.scam_g18.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean hasPrincipal = (request.getUserPrincipal() != null);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        // Valores por defecto siempre presentes para el header (Mustache falla si no existen)
        model.addAttribute("isUserLoggedIn", false);
        model.addAttribute("userId", "");
        model.addAttribute("userName", "");
        model.addAttribute("userProfileImage", "/img/descarga.jpg");
        model.addAttribute("canCreateEvent", false);
        model.addAttribute("isAdmin", false);

        if (hasPrincipal) {
            var currentUser = userService.getCurrentAuthenticatedUser().orElse(null);
            // Solo se considera "logueado" si el usuario existe en nuestra BD
            // (los usuarios PENDING con Google aún no están registrados)
            if (currentUser != null) {
                model.addAttribute("isUserLoggedIn", true);
                model.addAttribute("userId", currentUser.getId());
                model.addAttribute("userName", currentUser.getUsername());
                model.addAttribute("userProfileImage", userService.getProfileImage(currentUser.getId()));

                boolean canCreateEvent = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("SUBSCRIBED"));
                model.addAttribute("canCreateEvent", canCreateEvent);

                boolean isAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));
                model.addAttribute("isAdmin", isAdmin);
            }
        }
    }

}
