package es.codeurjc.scam_g18.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @GetMapping("/register/check-availability")
    @ResponseBody
    public Map<String, Boolean> checkAvailability(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email) {

        Map<String, Boolean> response = new HashMap<>();

        boolean usernameTaken = userService.usernameExists(username);
        boolean emailTaken = userService.emailExists(email);

        response.put("usernameTaken", usernameTaken);
        response.put("emailTaken", emailTaken);
        response.put("available", !usernameTaken && !emailTaken);

        return response;
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam String gender, @RequestParam String birthDate,
            @RequestParam String country, @RequestParam(value = "image", required = false) MultipartFile imageFile)
            throws IOException, java.sql.SQLException {

        if (userService.usernameExists(username)) {
            return "redirect:/register?error=usernameExists";
        }

        if (userService.emailExists(email)) {
            return "redirect:/register?error=emailExists";
        }

        boolean registered = userService.registerUser(username, email, password, gender, birthDate, country, imageFile);
        if (!registered) {
            return "redirect:/register?error=userExists";
        }

        emailService.newAccountMessage(email, username);

        return "redirect:/";
    }
}
