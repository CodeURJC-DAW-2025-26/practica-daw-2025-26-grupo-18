package es.codeurjc.scam_g18.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String viewCart(Model model, @RequestParam(required = false) String error) {
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
        model.addAttribute("errorNoSeats", "eventFull".equals(error));

        return "cart";
    }

    @PostMapping("/cart/add/course/{id}")
    public String addCourseToCart(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return "redirect:/courses";
        }
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
            try {
                cartService.addEventToOrder(order, eventOpt.get());
            } catch (IllegalStateException e) {
                return "redirect:/event/" + id + "?error=full";
            }
        }

        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{itemId}")
    public String removeItemFromCart(@PathVariable Long itemId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.removeItemFromOrder(order, itemId);

        return "redirect:/cart";
    }

    @PostMapping("/cart/checkout")
    public String checkout(@RequestParam String cardName,
            @RequestParam String billingEmail,
            @RequestParam String cardNumber,
            @RequestParam String cardExpiry) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        try {
            cartService.processPayment(order, cardName, billingEmail, cardNumber, cardExpiry);
        } catch (IllegalStateException e) {
            return "redirect:/cart?error=eventFull";
        }

        return "redirect:/";
    }

    private User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser().orElse(null);
    }
}
