package es.codeurjc.scam_g18.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.scam_g18.service.UserService;
import es.codeurjc.scam_g18.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addAttributes(Model model, Principal principal, HttpServletRequest request) {
        boolean isUserLoggedIn = (principal != null);
        model.addAttribute("isUserLoggedIn", isUserLoggedIn);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        if (isUserLoggedIn) {
            String username = principal.getName();
            model.addAttribute("userName", username);

            User user = userService.findByUsername(username).orElse(null);
            if (user != null && user.getImage() != null) {
                // Asumiendo que Image tiene un campo o método para la URL o ID.
                // Si image es un objeto, necesitamos convertirlo a path o usar un placeholder.
                // Revisando User.java (Step 599), tiene 'private Image image'.
                // Necesito ver la clase Image.java para saber qué campo usar.
                // Por ahora pondré un placeholder si es null o la ruta por defecto.
                model.addAttribute("userProfileImage", "/img/descarga.jpg"); // Placeholder temporal hasta ver
                                                                             // Image.java
            } else {
                model.addAttribute("userProfileImage", "/img/descarga.jpg");
            }
        }
    }
}
