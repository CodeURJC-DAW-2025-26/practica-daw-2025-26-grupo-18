package es.codeurjc.scam_g18.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.CartService;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.EventService;
import es.codeurjc.scam_g18.service.UserService;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private EventService eventService;

    @GetMapping("/cart")
    public String viewCart(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);

        // Calcular subtotales y totales para la vista
        int subtotalCents = cartService.calculateSubtotal(order);
        int taxCents = cartService.calculateTax(order);
        int totalCents = cartService.calculateTotal(order);

        model.addAttribute("order", order);
        model.addAttribute("subtotal", cartService.formatPriceInEuros(subtotalCents));
        model.addAttribute("tax", cartService.formatPriceInEuros(taxCents));
        model.addAttribute("total", cartService.formatPriceInEuros(totalCents));

        return "cart";
    }

    @PostMapping("/cart/add/course/{id}")
    public String addCourseToCart(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Course course = courseService.getCourseById(id);
        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.addCourseToOrder(order, course);

        return "redirect:/cart";
    }

    @PostMapping("/cart/add/event/{id}")
    public String addEventToCart(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isPresent()) {
            Order order = cartService.getOrCreatePendingOrder(currentUser);
            cartService.addEventToOrder(order, eventOpt.get());
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.processPayment(order);

        return "redirect:/profile/" + currentUser.getId();
    }

    private User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser().orElse(null);
    }
}
