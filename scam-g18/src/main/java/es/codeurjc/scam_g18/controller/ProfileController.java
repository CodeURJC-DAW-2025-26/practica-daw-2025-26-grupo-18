package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import java.security.Principal;
import java.util.Collections;
import es.codeurjc.scam_g18.service.UserService;
import es.codeurjc.scam_g18.model.User;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/courses/subscribed")
    public String subscribedCourses(Model model, Principal principal) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username).orElse(null);

            if (user != null) {
                // TODO: Recuperar cursos reales. Por ahora lista vacía.
                model.addAttribute("courses", Collections.emptyList());
                model.addAttribute("userName", user.getUsername());
            }
        }
        return "subscribedCourses";
    }
}
