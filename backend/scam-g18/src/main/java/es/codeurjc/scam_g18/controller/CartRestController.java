package es.codeurjc.scam_g18.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.dto.CheckoutRequestDTO;
import es.codeurjc.scam_g18.dto.OrderDTO;
import es.codeurjc.scam_g18.dto.OrderMapper;
import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.CartService;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.EventService;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart API", description = "Shopping cart and checkout endpoints")
public class CartRestController {

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

    // Returns the authenticated user's pending order info
    @GetMapping
    @Operation(summary = "Get cart", description = "Returns the current authenticated user's pending cart order.")
    public ResponseEntity<OrderDTO> getCart() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Order order = cartService.getOrCreatePendingOrder(currentUser);
        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // Adds a course to the current user's pending order
    @PostMapping("/courses/{id}")
    @Operation(summary = "Add course to cart", description = "Adds a course to the authenticated user's pending order.")
    public ResponseEntity<?> addCourseToCart(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Course course;
        try {
            course = courseService.getCourseById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.addCourseToOrder(order, course);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // Adds an event to the pending order
    @PostMapping("/events/{id}")
    @Operation(summary = "Add event to cart", description = "Adds an event to the authenticated user's pending order.")
    public ResponseEntity<?> addEventToCart(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<Event> eventOpt = eventService.getEventById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Event event = eventOpt.get();
        Order order = cartService.getOrCreatePendingOrder(currentUser);
        try {
            cartService.addEventToOrder(order, event);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("EVENT_FULL");
        }

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // Adds the premium subscription to the order
    @PostMapping("/subscriptions")
    @Operation(summary = "Add subscription to cart", description = "Adds a subscription line item to the authenticated user's pending order.")
    public ResponseEntity<OrderDTO> addSubscriptionToCart() {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.addSubscriptionToOrder(order);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // Removes an item from the cart
    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove cart item", description = "Removes an item from the authenticated user's cart by item id.")
    public ResponseEntity<OrderDTO> removeItemFromCart(@PathVariable Long itemId) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);
        cartService.removeItemFromOrder(order, itemId);

        return ResponseEntity.ok(orderMapper.toDTO(order));
    }

    // Processes payment (expects JSON body)
    @PostMapping("/payments")
    @Operation(summary = "Checkout", description = "Processes payment and finalizes the current pending order.")
    public ResponseEntity<?> checkout(
            @RequestBody CheckoutRequestDTO checkoutRequest,
            HttpServletRequest request) {

        if (checkoutRequest == null) {
            return ResponseEntity.badRequest().body("Invalid request");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        Order order = cartService.getOrCreatePendingOrder(currentUser);

        try {
            cartService.processPayment(order, checkoutRequest);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body("EVENT_FULL");
        }

        // Reload user details in Spring Security session
        if (currentUser.getUsername() != null) {
            userService.refreshUserSession(currentUser.getUsername(), request);
        }

        return ResponseEntity.ok().build();
    }

    private User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser().orElse(null);
    }
}
