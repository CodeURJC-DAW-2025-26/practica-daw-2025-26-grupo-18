package es.codeurjc.scam_g18.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
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
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean isUserLoggedIn = (request.getUserPrincipal() != null);
        model.addAttribute("isUserLoggedIn", isUserLoggedIn);

        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            model.addAttribute("_csrf", csrfToken);
        }

        if (isUserLoggedIn) {
            String username = request.getUserPrincipal().getName();
            model.addAttribute("userName", username);

            User user = userService.findByUsername(username).orElse(null);
            if (user != null) {
                model.addAttribute("userProfileImage", userService.getProfileImage(user.getId()));
            } else {
                model.addAttribute("userProfileImage", "/img/descarga.jpg");
            }
        }
    }

}
