package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.scam_g18.service.EmailService;
import es.codeurjc.scam_g18.service.UserService;

import java.io.IOException;

@Controller
public class RegisterController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String registerUser(@RequestParam(required = false) String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password, @RequestParam(required = false) String gender,
            @RequestParam(required = false) String birthDate,
            @RequestParam(required = false) String country,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws IOException, java.sql.SQLException {

        String validationErrors = userService.validateUserAttributes(username, email, password, birthDate, gender,
                country);
        if (validationErrors != null) {
            redirectAttributes.addFlashAttribute("error", validationErrors);
            return "redirect:/register";
        }

        boolean registered = userService.registerUser(username, email, password, gender, birthDate, country, imageFile);
        if (!registered) {
            redirectAttributes.addFlashAttribute("error", "El nombre de usuario o correo electrónico ya existen.");
            return "redirect:/register";
        }

        emailService.newAccountMessage(email, username);

        return "redirect:/login";
    }
}
