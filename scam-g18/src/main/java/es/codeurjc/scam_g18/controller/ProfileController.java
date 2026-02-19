package es.codeurjc.scam_g18.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.UserService;

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
                model.addAttribute("courses", Collections.emptyList());
                model.addAttribute("userName", user.getUsername());
            }
        }
        return "subscribedCourses";
    }

    @GetMapping("/profile/{id}")
    public String profile(Model model, @PathVariable long id) {
        Optional<User> user = userService.findById(id);
        if (user.isPresent()) {
            model.addAttribute("user", user.get());
            model.addAttribute("profileImage", userService.getProfileImage(id));
        }
        return "profile";
    }
}
