package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class IndexController {


    // Displays the application's home page.
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    // Displays a generic error page with a user-facing message.
    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        return "error";
    }

    // Displays forbidden-access error page when role checks deny access.
    @GetMapping("/error/forbidden")
    public String handleForbiddenError(Model model, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        model.addAttribute("status", HttpServletResponse.SC_FORBIDDEN);
        model.addAttribute("error", "Forbidden");
        model.addAttribute("message", "Acceso denegado: esta página está protegida por control de roles.");
        return "error";
    }
}
