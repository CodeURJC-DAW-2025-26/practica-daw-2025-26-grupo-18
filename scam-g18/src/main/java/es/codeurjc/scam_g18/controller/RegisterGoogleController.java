package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/register/google")
public class RegisterGoogleController {

    @Autowired
    private UserService userService;

    /**
     * Muestra el formulario para completar los datos del usuario registrado con
     * Google.
     * Solo accesible para usuarios con ROLE_PENDING (autenticados con Google pero
     * sin cuenta).
     */
    @GetMapping
    public String showGoogleRegisterForm(Model model,
            @AuthenticationPrincipal OAuth2User oAuth2User,
            @RequestParam(required = false) String error) {

        String email = oAuth2User.getAttribute("email");
        model.addAttribute("googleEmail", email);
        model.addAttribute("googleSuggestedUsername", buildSuggestedUsername(oAuth2User, email));
        model.addAttribute("googleSuggestedCountry", buildSuggestedCountry(oAuth2User));

        if ("userExists".equals(error)) {
            model.addAttribute("errorMsg",
                    "El nombre de usuario ya está en uso o este correo ya tiene una cuenta. Por favor, prueba con otro nombre de usuario o inicia sesión normalmente.");
        }

        return "registerGoogle";
    }

    private String buildSuggestedUsername(OAuth2User oAuth2User, String email) {
        String suggestedUsername = oAuth2User.getAttribute("given_name");

        if (suggestedUsername == null || suggestedUsername.isBlank()) {
            suggestedUsername = oAuth2User.getAttribute("name");
        }

        if ((suggestedUsername == null || suggestedUsername.isBlank()) && email != null && email.contains("@")) {
            suggestedUsername = email.substring(0, email.indexOf('@'));
        }

        if (suggestedUsername == null) {
            return "";
        }

        return suggestedUsername.trim().replaceAll("\\s+", "");
    }

    private String buildSuggestedCountry(OAuth2User oAuth2User) {
        String localeAttribute = oAuth2User.getAttribute("locale");

        if (localeAttribute == null || localeAttribute.isBlank()) {
            return "";
        }

        Locale googleLocale = Locale.forLanguageTag(localeAttribute.replace('_', '-'));
        if (googleLocale.getCountry() == null || googleLocale.getCountry().isBlank()) {
            return "";
        }

        return googleLocale.getDisplayCountry(Locale.of("es", "ES"));
    }

    /**
     * Procesa el formulario de registro completado.
     * Crea el usuario en la BD, autentica la sesión con sus roles reales y redirige
     * a "/".
     */
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
            HttpServletResponse response) throws IOException, SQLException {

        String email = oAuth2User.getAttribute("email");

        boolean registered = userService.registerUser(username, email, password, gender, birthDate, country, imageFile);

        if (!registered) {
            return "redirect:/register/google?error=userExists";
        }

        // Registro exitoso: cargamos el usuario recién creado para obtener sus roles
        // reales
        User newUser = userService.findByEmail(email).orElseThrow();

        List<GrantedAuthority> authorities = newUser.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());

        // Construimos el OAuth2User con los roles reales
        DefaultOAuth2User authenticatedUser = new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");

        // Creamos el token de autenticación OAuth2 (registrationId = "google")
        OAuth2AuthenticationToken authToken = new OAuth2AuthenticationToken(authenticatedUser, authorities, "google");

        // Establecemos la autenticación en el SecurityContext y la guardamos en sesión
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authToken);
        SecurityContextHolder.setContext(securityContext);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

        return "redirect:/";
    }

    /**
     * Cancela el registro: limpia la sesión PENDING y redirige al inicio sin
     * iniciar sesión.
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
