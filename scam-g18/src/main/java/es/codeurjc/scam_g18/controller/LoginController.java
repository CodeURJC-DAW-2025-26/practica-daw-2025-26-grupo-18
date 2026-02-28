package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.scam_g18.service.UserService;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String error) {
        if ("oauth2error".equals(error)) {
            model.addAttribute("error", "Error en el inicio de sesión con Google. Su cuenta podría estar bloqueada.");
        }
        model.addAttribute("isRegister", false);
        return "createuser"; // Usamos createuser.html como página unificada de login/registro
    }

    @GetMapping("/loginerror")
    public String loginError(jakarta.servlet.http.HttpServletRequest request, Model model) {
        Object exception = request.getSession()
                .getAttribute(org.springframework.security.web.WebAttributes.AUTHENTICATION_EXCEPTION);

        String errorMessage = "Usuario o contraseña incorrectos";
        if (exception instanceof org.springframework.security.authentication.DisabledException) {
            errorMessage = "Su cuenta ha sido bloqueada. Por favor, contacte con el administrador.";
        }

        model.addAttribute("error", errorMessage);
        model.addAttribute("isRegister", false);
        return "createuser";
    }

    @GetMapping("/register")
    public String register(Model model,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String error) {
        if ("usernameExists".equals(error)) {
            model.addAttribute("error", "Ese nombre de usuario ya está en uso.");
        } else if ("emailExists".equals(error)) {
            model.addAttribute("error", "Ese correo electrónico ya está registrado.");
        } else if ("userExists".equals(error)) {
            model.addAttribute("error", "Ya existe una cuenta con ese usuario o correo.");
        }

        model.addAttribute("listaPaises", userService.getAllCountries());

        model.addAttribute("isRegister", true);
        return "createuser";
    }
}
