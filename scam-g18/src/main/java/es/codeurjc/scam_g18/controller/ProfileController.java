package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.security.Principal;
import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.ImageService;
import es.codeurjc.scam_g18.service.UserService;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

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

    @PostMapping("/profile/{id}/edit")
    public String editProfile(@PathVariable long id,
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) MultipartFile imageFile) throws IOException, SQLException {

        Optional<User> optUser = userService.findById(id);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (username != null)
                user.setUsername(username);
            if (email != null)
                user.setEmail(email);
            if (country != null && !country.isBlank()) {
                user.setCountry(country);
            }
            if (imageFile != null && !imageFile.isEmpty()) {
                user.setImage(imageService.saveImage(imageFile));
            }
            userService.save(user);
        }

        return "redirect:/profile/" + id;
    }

}
