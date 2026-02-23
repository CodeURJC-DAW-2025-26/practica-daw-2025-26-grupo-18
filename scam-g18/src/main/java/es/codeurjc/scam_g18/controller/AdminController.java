package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ModelAttribute
    public void addCommonAttributes(Model model) {
        model.addAttribute("reviews", adminService.getAllReviews());
        model.addAttribute("pendingCourses", adminService.getPendingCourses());
    }

    @GetMapping
    public String adminDashboard() {
        return "adminDashboard";
    }

    @GetMapping("/users/search")
    public String searchUser(@RequestParam String name, Model model) {
        var user = adminService.findUserByUsername(name);

        model.addAttribute("searchQuery", name);

        if (user.isPresent()) {
            model.addAttribute("searchedUser", user.get());
        } else {
            model.addAttribute("searchError", "Usuario " + name + " no encontrado:");
        }
        return "adminDashboard";
    }

    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return "redirect:/admin";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        adminService.unbanUser(id);
        return "redirect:/admin";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return "redirect:/admin";
    }

    @PostMapping("/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id) {
        adminService.approveCourse(id);
        return "redirect:/admin";
    }

    @PostMapping("/courses/{id}/reject")
    public String rejectCourse(@PathVariable Long id) {
        adminService.rejectCourse(id);
        return "redirect:/admin";
    }
}