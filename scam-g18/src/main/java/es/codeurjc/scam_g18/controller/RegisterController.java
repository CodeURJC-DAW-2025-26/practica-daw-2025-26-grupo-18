package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import es.codeurjc.scam_g18.repository.UserRepository;
import es.codeurjc.scam_g18.repository.RoleRepository;
import es.codeurjc.scam_g18.repository.ImageRepository;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.model.Role;
import es.codeurjc.scam_g18.model.Image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Controller
public class RegisterController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email,
            @RequestParam String password, @RequestParam String gender, @RequestParam String birthDate,
            @RequestParam String country, @RequestParam("image") MultipartFile imageFile) throws IOException {

        if (userRepository.findByUsername(username).isPresent()) {
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
            Path imageFolder = Paths.get("src/main/resources/static/img/person");
            if (!Files.exists(imageFolder)) {
                Files.createDirectories(imageFolder);
            }
            
            String fileName = "user_" + System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
            Path imagePath = imageFolder.resolve(fileName);
            imageFile.transferTo(imagePath);
            
            Image image = new Image("/img/person/" + fileName);
            imageRepository.save(image);
            newUser.setImage(image);
        }

        Role userRole = roleRepository.findByName("USER").orElseGet(() -> {
            Role newRole = new Role("USER");
            return roleRepository.save(newRole);
        });

        newUser.getRoles().add(userRole);

        userRepository.save(newUser);

        return "redirect:/login";
    }
}
