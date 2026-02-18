package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("isRegister", false);
        return "createuser"; // Usamos createuser.html como página unificada de login/registro
    }

    @GetMapping("/loginerror")
    public String loginError(Model model) {
        model.addAttribute("error", "Usuario o contraseña incorrectos");
        model.addAttribute("isRegister", false); // Error happens during login
        return "createuser";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("isRegister", true);
        return "createuser";
    }
}
