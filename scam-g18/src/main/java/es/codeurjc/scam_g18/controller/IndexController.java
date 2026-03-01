package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

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
}
