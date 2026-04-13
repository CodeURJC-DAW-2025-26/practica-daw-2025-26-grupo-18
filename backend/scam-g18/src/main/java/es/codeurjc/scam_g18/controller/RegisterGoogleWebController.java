package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.EmailService;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/register/google")
public class RegisterGoogleWebController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String showGoogleRegisterForm(Model model,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam(required = false) String error) {

        String email = oAuth2User.getAttribute("email");
        model.addAttribute("googleEmail", email);
        model.addAttribute("googleSuggestedUsername", userService.buildSuggestedUsername(oAuth2User, email));
        model.addAttribute("googleSuggestedCountry", userService.buildSuggestedCountry(oAuth2User));

        if ("usernameExists".equals(error)) {
            model.addAttribute("errorMsg",
                    "Ese nombre de usuario ya está en uso. Por favor, prueba con otro.");
        } else if ("emailExists".equals(error)) {
            model.addAttribute("errorMsg",
                    "Ya existe una cuenta con este correo de Google. Inicia sesión normalmente.");
        } else if ("userExists".equals(error)) {
            model.addAttribute("errorMsg",
                    "El nombre de usuario ya está en uso o este correo ya tiene una cuenta. Por favor, prueba con otro nombre de usuario o inicia sesión normalmente.");
        }

        return "registerGoogle";
    }

    @PostMapping
    public String completeGoogleRegistration(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String gender,
            @RequestParam String birthDate,
            @RequestParam String country,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            HttpServletRequest request,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes)
            throws IOException, SQLException {

        String email = oAuth2User.getAttribute("email");

        // Unified validation logic from Service layer
        String validationErrors = userService.validateUserAttributes(username, email, password, birthDate, gender,
                country);
        if (validationErrors != null) {
            redirectAttributes.addFlashAttribute("errorMsg", validationErrors);
            return "redirect:/register/google";
        }

        if (userService.usernameExists(username)) {
            return "redirect:/register/google?error=usernameExists";
        }

        if (userService.emailExists(email)) {
            return "redirect:/register/google?error=emailExists";
        }

        boolean registered = userService.registerUser(username, email, password, gender, birthDate, country, imageFile);

        if (!registered) {
            return "redirect:/register/google?error=userExists";
        }

        emailService.newAccountMessage(email, username);

        User newUser = userService.findByEmail(email).orElseThrow();
        userService.authenticateOAuth2User(newUser, oAuth2User, request);

        return "redirect:/";
    }

    /**
     * Cancels registration: clears the PENDING session and redirects to home
     * without signing in.
     */
    @GetMapping("/cancel")
    public String cancelGoogleRegistration(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }
}
