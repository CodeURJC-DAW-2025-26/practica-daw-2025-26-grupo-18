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

import es.codeurjc.scam_g18.dto.CheckoutRequestDTO;
import es.codeurjc.scam_g18.dto.CourseDTO;
import es.codeurjc.scam_g18.dto.EventDTO;
import es.codeurjc.scam_g18.dto.OrderDTO;
import es.codeurjc.scam_g18.mapper.CourseMapper;
import es.codeurjc.scam_g18.mapper.EventMapper;
import es.codeurjc.scam_g18.mapper.OrderMapper;
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

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private EventMapper eventMapper;

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
        
        // Convert to DTO and overwrite the "order" attribute so the view gets the DTO
        OrderDTO orderDto = orderMapper.toDTO(order);
        model.addAttribute("order", orderDto);
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
        
        CourseDTO courseDto = courseMapper.toDTO(course);
        Order order = cartService.getOrCreatePendingOrder(currentUser);
        
        // Pasamos la entidad 'course' original (manejada por JPA) a CartService, en lugar de 
        // mapear de vuelta courseDto, para evitar errores al relacionar OrderItem con Course al guardar.
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
            Event event = eventOpt.get();
            EventDTO eventDto = eventMapper.toDTO(event);
            Order order = cartService.getOrCreatePendingOrder(currentUser);
            try {
                // Pasamos la entidad 'event' original a CartService para no romper JPA.
                cartService.addEventToOrder(order, event);
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
            CheckoutRequestDTO checkoutRequest,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);

        try {
            cartService.processPayment(order, 
                checkoutRequest.getCardName(), 
                checkoutRequest.getBillingEmail(), 
                checkoutRequest.getCardNumber(), 
                checkoutRequest.getCardExpiry(), 
                checkoutRequest.getCardCvv());
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
