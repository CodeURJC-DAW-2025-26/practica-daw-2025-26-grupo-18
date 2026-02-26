package es.codeurjc.scam_g18.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

@Controller
public class IndexController {


    // Muestra la página principal de la aplicación.
    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    // Muestra una página de error genérica con un mensaje para el usuario.
    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("errorMessage", "Ha ocurrido un error inesperado. Por favor, inténtalo de nuevo más tarde.");
        return "error";
    }
}
