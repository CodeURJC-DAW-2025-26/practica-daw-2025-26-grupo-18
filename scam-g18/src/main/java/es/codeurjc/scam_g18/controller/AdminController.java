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

    // Rellena el modelo del panel con usuarios, cursos, eventos, reseñas y pedidos.
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

    // Muestra el dashboard de administración con filtros opcionales.
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

    // Busca usuarios por nombre y mantiene activa la pestaña de usuarios.
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String name, Model model) {
        populateModel(model, name, null, null, "users");
        return "adminDashboard";
    }

    // Bloquea a un usuario por su identificador.
    @PostMapping("/users/{id}/ban")
    public String banUser(@PathVariable Long id) {
        adminService.banUser(id);
        return "redirect:/admin?activeTab=users";
    }

    // Desbloquea a un usuario por su identificador.
    @PostMapping("/users/{id}/unban")
    public String unbanUser(@PathVariable Long id) {
        adminService.unbanUser(id);
        return "redirect:/admin?activeTab=users";
    }

    // ---- Course actions ----

    // Aprueba un curso pendiente.
    @PostMapping("/courses/{id}/approve")
    public String approveCourse(@PathVariable Long id) {
        adminService.approveCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    // Rechaza un curso pendiente.
    @PostMapping("/courses/{id}/reject")
    public String rejectCourse(@PathVariable Long id) {
        adminService.rejectCourse(id);
        return "redirect:/admin?activeTab=courses";
    }

    // ---- Event actions ----

    // Aprueba un evento pendiente.
    @PostMapping("/events/{id}/approve")
    public String approveEvent(@PathVariable Long id) {
        adminService.approveEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    // Rechaza un evento pendiente.
    @PostMapping("/events/{id}/reject")
    public String rejectEvent(@PathVariable Long id) {
        adminService.rejectEvent(id);
        return "redirect:/admin?activeTab=events";
    }

    // ---- Review actions ----

    // Elimina una reseña por su identificador.
    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminService.deleteReview(id);
        return "redirect:/admin";
    }
}
