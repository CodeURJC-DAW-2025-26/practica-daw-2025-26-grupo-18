package es.codeurjc.scam_g18.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import es.codeurjc.scam_g18.service.UserService;

@ControllerAdvice
public class GlobalModelAttributes {
    
    private final UserService userService;
    
    public GlobalModelAttributes(UserService userService) {
        this.userService = userService;
    }
    
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("isUserLoggedIn", userService.isUserLoggedIn());
        model.addAttribute("userName", userService.getCurrentUserName());
        model.addAttribute("userProfileImage", userService.getCurrentUserProfileImage());
    }
}