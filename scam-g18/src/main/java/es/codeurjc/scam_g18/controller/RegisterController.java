package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import es.codeurjc.scam_g18.service.UserService;
import es.codeurjc.scam_g18.service.RolerService;
import es.codeurjc.scam_g18.service.ImageService;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.model.Role;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private RolerService rolerService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam String gender, @RequestParam String birthDate,
            @RequestParam String country, @RequestParam("image") MultipartFile imageFile)
            throws IOException, java.sql.SQLException {

        if (userService.findByUsername(username).isPresent()) {
            return "redirect:/login?error=userExists";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setGender(gender);
        newUser.setBirthDate(LocalDate.parse(birthDate));
        newUser.setCountry(country);

        if (!imageFile.isEmpty()) {
            newUser.setImage(imageService.saveImage(imageFile));
        }

        Role userRole = rolerService.findByName("USER").orElseGet(() -> {
            Role newRole = new Role("USER");
            rolerService.save(newRole);
            return newRole;
        });

        newUser.getRoles().add(userRole);

        userService.save(newUser);

        return "redirect:/";
    }
}
