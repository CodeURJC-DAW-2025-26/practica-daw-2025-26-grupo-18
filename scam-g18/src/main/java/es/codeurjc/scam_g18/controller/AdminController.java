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

    // Populates dashboard model with users, courses, events, reviews, and orders.
    private void populateModel(Model model, String userQuery, String courseQuery, String eventQuery, String activeTab) {
        // Users
        if (userQuery != null && !userQuery.isBlank()) {
            model.addAttribute("users", adminService.searchUsers(userQuery, 0, PAGE_SIZE));
            model.addAttribute("hasMoreUsers", adminService.getTotalSearchUsersCount(userQuery) > PAGE_SIZE);
        } else {
            model.addAttribute("users", adminService.getAllUsers(0, PAGE_SIZE));
            model.addAttribute("hasMoreUsers", adminService.getTotalUsersCount() > PAGE_SIZE);
        }

        // Courses (pending first)
        if (courseQuery != null && !courseQuery.isBlank()) {
            model.addAttribute("courses", adminService.searchCourses(courseQuery, 0, PAGE_SIZE));
            model.addAttribute("hasMoreCourses", adminService.getTotalSearchCoursesCount(courseQuery) > PAGE_SIZE);
        } else {
            model.addAttribute("courses", adminService.getAllCoursesSortedByStatus(0, PAGE_SIZE));
            model.addAttribute("hasMoreCourses", adminService.getTotalCoursesCount() > PAGE_SIZE);
        }

        // Events (pending first)
        if (eventQuery != null && !eventQuery.isBlank()) {
            model.addAttribute("events", adminService.searchEvents(eventQuery, 0, PAGE_SIZE));
            model.addAttribute("hasMoreEvents", adminService.getTotalSearchEventsCount(eventQuery) > PAGE_SIZE);
        } else {
            model.addAttribute("events", adminService.getAllEventsSortedByStatus(0, PAGE_SIZE));
            model.addAttribute("hasMoreEvents", adminService.getTotalEventsCount() > PAGE_SIZE);
        }

        model.addAttribute("userQuery", userQuery != null ? userQuery : "");
        model.addAttribute("courseQuery", courseQuery != null ? courseQuery : "");
        model.addAttribute("eventQuery", eventQuery != null ? eventQuery : "");

        // Orders
        model.addAttribute("orders", adminService.getAllOrdersSortedByDate(0, PAGE_SIZE));
        model.addAttribute("hasMoreOrders", adminService.getTotalOrdersCount() > PAGE_SIZE);

        model.addAttribute("activeTab", activeTab != null ? activeTab : "users");
        model.addAttribute("reviews", adminService.getAllReviews());
        model.addAttribute("orders", adminService.getAllOrdersSortedByDate());
    }

    // Displays the admin dashboard with optional filters.
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

    // Searches users by name and keeps the users tab active.
    @GetMapping("/users/search")
    public String searchUser(@RequestParam String name, Model model) {
        populateModel(model, name, null, null, "users");
        return "adminDashboard";
    }

    // AJAX endpoint for user pagination
    @GetMapping("/api/users")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getUsersApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {

        java.util.List<es.codeurjc.scam_g18.model.User> users;
        if (query != null && !query.isBlank()) {
            users = adminService.searchUsers(query, page, PAGE_SIZE);
        } else {
            users = adminService.getAllUsers(page, PAGE_SIZE);
        }

        java.util.List<java.util.Map<String, Object>> userList = new java.util.ArrayList<>();
        for (es.codeurjc.scam_g18.model.User u : users) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("isActive", u.getIsActive());
            map.put("isSubscribed", u.getIsSubscribed());
            userList.add(map);
        }

        return org.springframework.http.ResponseEntity.ok(userList);
    }

    // AJAX endpoint for event pagination
    @GetMapping("/api/events")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminEventsApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {

        java.util.List<es.codeurjc.scam_g18.model.Event> events;
        if (query != null && !query.isBlank()) {
            events = adminService.searchEvents(query, page, PAGE_SIZE);
        } else {
            events = adminService.getAllEventsSortedByStatus(page, PAGE_SIZE);
        }

        java.util.List<java.util.Map<String, Object>> eventList = new java.util.ArrayList<>();
        for (es.codeurjc.scam_g18.model.Event e : events) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", e.getId());
            map.put("title", e.getTitle());
            map.put("category", e.getCategory());
            map.put("isPendingReview", e.getStatus() == es.codeurjc.scam_g18.model.Status.PENDING_REVIEW);
            map.put("status", e.getStatus() != null ? e.getStatus().toString() : "");
            map.put("creatorUsername", e.getCreator() != null ? e.getCreator().getUsername() : "");
            eventList.add(map);
        }

        return org.springframework.http.ResponseEntity.ok(eventList);
    }

    // AJAX endpoint for course pagination
    @GetMapping("/api/courses")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminCoursesApi(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "0") int page) {

        java.util.List<es.codeurjc.scam_g18.model.Course> courses;
        if (query != null && !query.isBlank()) {
            courses = adminService.searchCourses(query, page, PAGE_SIZE);
        } else {
            courses = adminService.getAllCoursesSortedByStatus(page, PAGE_SIZE);
        }

        java.util.List<java.util.Map<String, Object>> courseList = new java.util.ArrayList<>();
        for (es.codeurjc.scam_g18.model.Course c : courses) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", c.getId());
            map.put("title", c.getTitle());
            map.put("shortDescription", c.getShortDescription());
            map.put("isPendingReview", c.getStatus() == es.codeurjc.scam_g18.model.Status.PENDING_REVIEW);
            map.put("status", c.getStatus() != null ? c.getStatus().toString() : "");
            map.put("creatorUsername", c.getCreator() != null ? c.getCreator().getUsername() : "");
            courseList.add(map);
        }
        return org.springframework.http.ResponseEntity.ok(courseList);
    }

    // AJAX endpoint for order pagination
    @GetMapping("/api/orders")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getAdminOrdersApi(
            @RequestParam(defaultValue = "0") int page) {

        java.util.List<es.codeurjc.scam_g18.model.Order> orders = adminService.getAllOrdersSortedByDate(page,
            PAGE_SIZE);

        java.util.List<java.util.Map<String, Object>> orderList = new java.util.ArrayList<>();
        for (es.codeurjc.scam_g18.model.Order o : orders) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", o.getId());
            if (o.getUser() != null)
                map.put("username", o.getUser().getUsername());
            map.put("billingFullName", o.getBillingFullName());
            map.put("billingEmail", o.getBillingEmail());
            map.put("status", o.getStatus() != null ? o.getStatus().toString() : "");

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter
                    .ofPattern("dd/MM/yyyy HH:mm");
            if (o.getPaidAt() != null) {
                map.put("paidAt", o.getPaidAt().format(formatter));
            } else if (o.getCreatedAt() != null) {
                map.put("createdAt", o.getCreatedAt().format(formatter));
            }
            map.put("paymentMethod", o.getPaymentMethod());
            map.put("paymentReference", o.getPaymentReference());
            map.put("totalAmountEuros", o.getTotalAmountEuros());
            orderList.add(map);
        }
        return org.springframework.http.ResponseEntity.ok(orderList);
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
