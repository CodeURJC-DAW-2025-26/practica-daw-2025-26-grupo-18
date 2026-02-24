package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private void populateModel(Model model, String userQuery, String courseQuery, String eventQuery, String activeTab) {
        // Users
        if (userQuery != null && !userQuery.isBlank()) {
            model.addAttribute("users", adminService.searchUsers(userQuery));
        } else {
            model.addAttribute("users", adminService.getAllUsers());
        }

        // Courses (pending first)
        if (courseQuery != null && !courseQuery.isBlank()) {
            model.addAttribute("courses", adminService.searchCourses(courseQuery));
        } else {
            model.addAttribute("courses", adminService.getAllCoursesSortedByStatus());
        }

        // Events (pending first)
        if (eventQuery != null && !eventQuery.isBlank()) {
            model.addAttribute("events", adminService.searchEvents(eventQuery));
        } else {
            model.addAttribute("events", adminService.getAllEventsSortedByStatus());
        }

        model.addAttribute("userQuery", userQuery != null ? userQuery : "");
        model.addAttribute("courseQuery", courseQuery != null ? courseQuery : "");
        model.addAttribute("eventQuery", eventQuery != null ? eventQuery : "");
        model.addAttribute("activeTab", activeTab != null ? activeTab : "users");
        model.addAttribute("reviews", adminService.getAllReviews());
        model.addAttribute("orders", adminService.getAllOrdersSortedByDate());
    }

    @GetMapping
    public String adminDashboard(
            @RequestParam(required = false) String userQuery,
            @RequestParam(required = false) String courseQuery,
            @RequestParam(required = false) String eventQuery,
            @RequestParam(required = false, defaultValue = "users") String activeTab,
            Model model) {
        populateModel(model, userQuery, courseQuery, eventQuery, activeTab);
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
        return "redirect:/admin?activeTab=users";
    }

    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        adminService.unbanUser(id);
        return "redirect:/admin?activeTab=users";
    }

    // ---- Course actions ----

    @PostMapping("/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id) {
        adminService.approveCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    @PostMapping("/courses/{id}/reject")
    public String rejectCourse(@PathVariable Long id) {
        adminService.rejectCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    // ---- Event actions ----

    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id) {
        adminService.approveEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id) {
        adminService.rejectEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    // ---- Review actions ----

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return "redirect:/admin";
    }
}
