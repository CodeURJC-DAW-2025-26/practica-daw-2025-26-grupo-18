package es.codeurjc.scam_g18.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;

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

    // Displays the authenticated user's cart with subtotal, taxes, and total.
    @GetMapping("/cart")
    public String viewCart(Model model, @RequestParam(required = false) String error) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);

        // Calculate subtotals and totals for the view
        model.addAllAttributes(cartService.getCartSummary(order, error));
        model.addAttribute("errorNoSeats", "eventFull".equals(error));

        return "cart";
    }

    // Adds a course to the current user's pending order.
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

    // Adds an event to the pending order and handles sold-out availability.
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

    // Adds the premium subscription to the authenticated user's pending order.
    @PostMapping("/cart/add/subscription")
    public String addSubscriptionToCart() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.addSubscriptionToOrder(order);

        return "redirect:/cart";
    }

    // Removes a specific item from the authenticated user's cart.
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

    // Processes payment for the pending order and completes purchase with
    // data validation.
    @PostMapping("/cart/checkout")
    public String checkout(
            @RequestParam(required = false) String cardName,
            @RequestParam(required = false) String billingEmail,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String cardExpiry,
            @RequestParam(required = false) String cardCvv,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);

        try {
            cartService.processPayment(order, cardName, billingEmail, cardNumber, cardExpiry, cardCvv);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Return to cart showing the errors at the top
            return "redirect:/cart";
        } catch (IllegalStateException e) {
            return "redirect:/cart?error=eventFull";
        }

        // Reload user details and update Spring Security session to reflect new roles
        // immediately
        if (currentUser.getUsername() != null) {
            userService.refreshUserSession(currentUser.getUsername(), request);
        }

        return "redirect:/";
    }

    // Returns the current authenticated user or null if there is no session.
    private User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser().orElse(null);
    }
}
