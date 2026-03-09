package es.codeurjc.scam_g18.controller;

import es.codeurjc.scam_g18.service.AdminService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final int PAGE_SIZE = 10;

    @Autowired
    private AdminService adminService;

    // Displays the admin dashboard with optional filters.
    @GetMapping
    public String adminDashboard(
            @RequestParam(required = false) String userQuery,
            @RequestParam(required = false) String courseQuery,
            @RequestParam(required = false) String eventQuery,
            @RequestParam(required = false, defaultValue = "users") String activeTab,
            Model model) {
        model.addAllAttributes(
                adminService.getDashboardViewData(userQuery, courseQuery, eventQuery, activeTab, PAGE_SIZE));
        return "adminDashboard";
    }

    // Searches users by name and keeps the users tab active.
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String name, Model model) {
        model.addAllAttributes(adminService.getDashboardViewData(name, null, null, "users", PAGE_SIZE));
        return "adminDashboard";
    }

    // AJAX endpoint for user pagination
    @GetMapping("/api/users")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getUsersApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {
        return org.springframework.http.ResponseEntity.ok(adminService.getUsersApiData(query, page, PAGE_SIZE));
    }

    // AJAX endpoint for event pagination
    @GetMapping("/api/events")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminEventsApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {
        return org.springframework.http.ResponseEntity.ok(adminService.getEventsApiData(query, page, PAGE_SIZE));
    }

    // AJAX endpoint for course pagination
    @GetMapping("/api/courses")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminCoursesApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {
        return org.springframework.http.ResponseEntity.ok(adminService.getCoursesApiData(query, page, PAGE_SIZE));
    }

    // AJAX endpoint for order pagination
    @GetMapping("/api/orders")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminOrdersApi(
            @RequestParam(defaultValue = "0") int page) {
        return org.springframework.http.ResponseEntity.ok(adminService.getOrdersApiData(page, PAGE_SIZE));
    }

    // Bans a user by identifier.
    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return "redirect:/admin?activeTab=users";
    }

    // Unbans a user by identifier.
    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        adminService.unbanUser(id);
        return "redirect:/admin?activeTab=users";
    }

    // ---- Course actions ----

    // Approves a pending course.
    @PostMapping("/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id) {
        adminService.approveCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    // Rejects a pending course.
    @PostMapping("/courses/{id}/reject")
    public String rejectCourse(@PathVariable Long id) {
        adminService.rejectCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    // ---- Event actions ----

    // Approves a pending event.
    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id) {
        adminService.approveEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    // Rejects a pending event.
    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id) {
        adminService.rejectEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    // ---- Review actions ----

    // Deletes a review by identifier.
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return "redirect:/admin";
    }
}
