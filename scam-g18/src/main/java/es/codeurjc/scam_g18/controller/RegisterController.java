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
    public String registerUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam String gender, @RequestParam String birthDate,
            @RequestParam String country, @RequestParam("image") MultipartFile imageFile)
            throws IOException, java.sql.SQLException {

        boolean registered = userService.registerUser(username, email, password, gender, birthDate, country, imageFile);
        if (!registered) {
            return "redirect:/login?error=userExists";
        }

        emailService.newAccountMessage(email, username);

        return "redirect:/";
    }
}
