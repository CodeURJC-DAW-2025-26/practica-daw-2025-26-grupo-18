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

    // Muestra el carrito del usuario autenticado con subtotal, impuestos y total.
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

    // Añade un curso al pedido pendiente del usuario actual.
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

    // Añade un evento al pedido pendiente y controla si no quedan plazas
    // disponibles.
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

    // Elimina un elemento concreto del carrito del usuario autenticado.
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

    // Procesa el pago del pedido pendiente y finaliza la compra con validación de
    // datos.
    @PostMapping("/cart/checkout")
    public String checkout(
            @RequestParam(required = false) String cardName,
            @RequestParam(required = false) String billingEmail,
            @RequestParam(required = false) String cardNumber,
            @RequestParam(required = false) String cardExpiry,
            @RequestParam(required = false) String cardCvv,
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

        return "redirect:/";
    }

    // Obtiene el usuario autenticado actual o null si no hay sesión.
    private User getCurrentUser() {
        return userService.getCurrentAuthenticatedUser().orElse(null);
    }
}
