package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Enrollment;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.EventRegistration;
import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.OrderItem;
import es.codeurjc.scam_g18.model.OrderStatus;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.EventRegistrationRepository;
import es.codeurjc.scam_g18.model.Subscription;
import es.codeurjc.scam_g18.model.SubscriptionStatus;
import es.codeurjc.scam_g18.repository.SubscriptionRepository;
import es.codeurjc.scam_g18.repository.OrderRepository;
import es.codeurjc.scam_g18.repository.OrderItemRepository;
import es.codeurjc.scam_g18.repository.RoleRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.model.Role;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private InvoicePdfService invoicePdfService;
    @Autowired
    private EmailService emailService;

    private static final int SUBSCRIPTION_PRICE_CENTS = 1000;
    private static final int SUBSCRIPTION_DURATION_DAYS = 30;

    // Crea un pedido pendiente vacío para el usuario.
    public Order createOrder(User user) {
        Order order = new Order(user, 0, OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        return orderRepository.save(order);
    }

    // Añade un curso al pedido y recalcula el subtotal.
    public void addCourseToOrder(Order order, Course course) {
        OrderItem item = new OrderItem(order, course, null, course.getPriceCents());
        order.getItems().add(item);
        order.setTotalAmountCents(calculateSubtotal(order));
        orderRepository.save(order);
    }

    // Añade un evento al pedido validando disponibilidad de plazas.
    public void addEventToOrder(Order order, Event event) {
        Event currentEvent = eventRepository.findById(event.getId()).orElse(event);
        if (!currentEvent.hasAvailableSeats()) {
            throw new IllegalStateException("EVENT_FULL");
        }

        OrderItem item = new OrderItem(order, null, event, event.getPriceCents());
        order.getItems().add(item);
        order.setTotalAmountCents(calculateSubtotal(order));
        orderRepository.save(order);
    }

    // Añade la suscripción al pedido si todavía no existe.
    public void addSubscriptionToOrder(Order order) {
        boolean hasSubscription = false;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                if (item.isSubscription()) {
                    hasSubscription = true;
                    break;
                }
            }
        }

        if (!hasSubscription) {
            OrderItem item = new OrderItem(order, null, null, SUBSCRIPTION_PRICE_CENTS, true);
            if (order.getItems() == null) {
                order.setItems(new ArrayList<>());
            }
            order.getItems().add(item);
            order.setTotalAmountCents(calculateSubtotal(order));
            orderRepository.save(order);
        }
    }

    @Transactional
    // Elimina un ítem del pedido y actualiza su total.
    public void removeItemFromOrder(Order order, Long itemId) {
        if (order.getItems() == null || itemId == null) {
            return;
        }

        Optional<OrderItem> itemOptional = orderItemRepository.findById(itemId);
        if (itemOptional.isEmpty()) {
            return;
        }

        OrderItem item = itemOptional.get();
        if (item.getOrder() == null || order.getId() == null || !order.getId().equals(item.getOrder().getId())) {
            return;
        }

        order.getItems().removeIf(orderItem -> orderItem.getId() != null && orderItem.getId().equals(itemId));
        orderItemRepository.delete(item);
        order.setTotalAmountCents(calculateSubtotal(order));
        orderRepository.save(order);
    }

    // Calcula el subtotal de un pedido sin impuestos.
    public int calculateSubtotal(Order order) {
        if (order.getItems() == null) {
            return 0;
        }
        int total = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getPriceAtPurchaseCents() != null) {
                total += item.getPriceAtPurchaseCents();
            }
        }
        return total;
    }

    // Calcula el impuesto aplicado al pedido.
    public int calculateTax(Order order) {
        return (int) (calculateSubtotal(order) * 0.21);
    }

    // Devuelve el total a pagar incluyendo impuestos.
    public int calculateTotal(Order order) {
        return calculateSubtotal(order) + calculateTax(order);
    }

    // Devuelve un resumen formateado del carrito para la vista
    public Map<String, Object> getCartSummary(Order order, String error) {
        Map<String, Object> summary = new HashMap<>();

        int subtotalCents = calculateSubtotal(order);
        int taxCents = calculateTax(order);
        int totalCents = calculateTotal(order);

        summary.put("order", order);
        summary.put("subtotal", formatPriceInEuros(subtotalCents));
        summary.put("tax", formatPriceInEuros(taxCents));
        summary.put("total", formatPriceInEuros(totalCents));
        summary.put("errorNoSeats", "eventFull".equals(error));

        return summary;
    }

    // Formatea una cantidad en céntimos a euros con dos decimales.
    public String formatPriceInEuros(int cents) {
        return String.format("%.2f", cents / 100.0);
    }

    @Transactional
    // Procesa el pago, activa compras y genera/manda factura.
    public void processPayment(Order order, String cardName, String billingEmail, String cardNumber,
            String cardExpiry, String cardCvv) {

        // --- BACKEND VALIDATION RULES ---
        StringBuilder errors = new StringBuilder();

        if (cardName == null || cardName.trim().isEmpty() || !cardName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            errors.append("El nombre en la tarjeta es inválido o está vacío.<br>");
        }

        if (billingEmail == null || !billingEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.append("El formato del email de facturación no es válido.<br>");
        }

        if (cardNumber == null || !cardNumber.matches("^\\d{16}$")) {
            errors.append("El número de tarjeta debe contener exactamente 16 dígitos.<br>");
        }

        if (cardExpiry == null || !cardExpiry.matches("^(0[1-9]|1[0-2])/\\d{2}$")) {
            errors.append("La fecha de caducidad debe tener formato MM/YY.<br>");
        } else {
            try {
                int expMonth = Integer.parseInt(cardExpiry.substring(0, 2));
                int expYear = 2000 + Integer.parseInt(cardExpiry.substring(3, 5));
                java.time.LocalDate now = java.time.LocalDate.now();
                int currentMonth = now.getMonthValue();
                int currentYear = now.getYear();

                if (expYear < currentYear || (expYear == currentYear && expMonth < currentMonth)) {
                    errors.append("La tarjeta de crédito está caducada.<br>");
                }
            } catch (Exception e) {
                errors.append("La fecha de caducidad no es una medida de tiempo válida.<br>");
            }
        }

        if (cardCvv == null || !cardCvv.matches("^\\d{3}$")) {
            errors.append("El CVV debe contener exactamente 3 dígitos.<br>");
        }

        // Check if any rule failed, throw exception
        if (errors.length() > 0) {
            throw new IllegalArgumentException(errors.toString());
        }

        if (order.getItems() == null || order.getItems().isEmpty()) {
            return;
        }

        for (OrderItem item : order.getItems()) {
            if (item.getEvent() != null) {
                Event event = eventRepository.findById(item.getEvent().getId()).orElse(null);
                if (event == null || !event.hasAvailableSeats()) {
                    throw new IllegalStateException("EVENT_FULL");
                }
            }
        }

        order.setStatus(OrderStatus.PAID);
        order.setTotalAmountCents(calculateTotal(order));
        order.setPaidAt(LocalDateTime.now());
        order.setPaymentMethod("SIMULATED_CARD");
        order.setPaymentReference(generatePaymentReference());
        order.setBillingFullName(cardName != null ? cardName.trim() : "");
        order.setBillingEmail(billingEmail != null ? billingEmail.trim() : "");
        order.setCardLast4(extractLast4Digits(cardNumber));
        order.setInvoicePending(true);
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            if (item.isSubscription()) {
                activateSubscription(order.getUser());
            } else {
                if (item.getCourse() != null) {
                    enrollInCourse(order.getUser(), item.getCourse());
                }
                if (item.getEvent() != null) {
                    registerForEvent(order.getUser(), item.getEvent());
                }
            }
        }

        try {
            byte[] invoicePdf = invoicePdfService.generateInvoicePdf(order);
            String recipient = order.getUser() != null ? order.getBillingEmail() : order.getUser().getEmail();
            String username = order.getUser() != null ? order.getUser().getUsername() : order.getBillingFullName();

            if (recipient != null && !recipient.isBlank()) {
                emailService.orderInvoiceMessage(recipient, username != null ? username : "cliente", order.getId(),
                        invoicePdf);
                order.setInvoicePending(false);
            } else {
                order.setInvoicePending(true);
            }
        } catch (Exception e) {
            order.setInvoicePending(true);
        }

        orderRepository.save(order);
    }

    // Genera una referencia única para el pago simulado.
    private String generatePaymentReference() {
        return "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // Extrae los 4 últimos dígitos de la tarjeta.
    private String extractLast4Digits(String cardNumber) {
        if (cardNumber == null) {
            return "0000";
        }

        String onlyDigits = cardNumber.replaceAll("\\D", "");
        if (onlyDigits.length() < 4) {
            return "0000";
        }

        return onlyDigits.substring(onlyDigits.length() - 4);
    }

    // Activa o amplía la suscripción premium del usuario.
    private void activateSubscription(User user) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserIdAndStatus(user.getId(),
                SubscriptionStatus.ACTIVE);

        Subscription subscription;
        if (existingSubscription.isPresent()) {
            subscription = existingSubscription.get();
            subscription.setStartDate(java.time.LocalDateTime.now());
            subscription.setEndDate(java.time.LocalDateTime.now().plusDays(SUBSCRIPTION_DURATION_DAYS));
        } else {
            subscription = new Subscription(user, java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now().plusDays(SUBSCRIPTION_DURATION_DAYS), SubscriptionStatus.ACTIVE);
        }

        // Actualizar rol de usuario a SUBSCRIBED si no lo tiene
        Role subscribedRole = roleRepository.findByName("SUBSCRIBED").orElseGet(() -> {
            Role newRole = new Role("SUBSCRIBED");
            roleRepository.save(newRole);
            return newRole;
        });

        boolean hasRole = user.getRoles().stream().anyMatch(r -> r.getName().equals("SUBSCRIBED"));
        if (!hasRole) {
            user.getRoles().add(subscribedRole);
            userService.save(user);
        }

        subscriptionRepository.save(subscription);
    }

    // Matricula al usuario en un curso tras la compra.
    private void enrollInCourse(User user, Course course) {
        Optional<Enrollment> existingOpt = enrollmentRepository.findByUserIdAndCourseId(user.getId(), course.getId());
        if (existingOpt.isPresent()) {
            Enrollment existing = existingOpt.get();
            if (!existing.isActive()) {
                existing.setEnrolledAt(LocalDateTime.now());
                existing.setExpiresAt(LocalDateTime.now().plusDays(30));
                enrollmentRepository.save(existing);
            }
        } else {
            Course managedCourse = courseRepository.findById(course.getId()).orElse(null);
            if (managedCourse != null) {
                Enrollment enrollment = new Enrollment(user, managedCourse);
                enrollmentRepository.save(enrollment);

                managedCourse.setSubscribersNumber(managedCourse.getSubscribersNumber() + 1);
                courseRepository.save(managedCourse);
            }
        }
    }

    // Registra al usuario en un evento tras la compra.
    private void registerForEvent(User user, Event event) {
        if (!eventRegistrationRepository.existsByUserIdAndEventId(user.getId(), event.getId())) {
            Event managedEvent = eventRepository.findById(event.getId()).orElse(null);
            if (managedEvent == null || !managedEvent.hasAvailableSeats()) {
                throw new IllegalStateException("EVENT_FULL");
            }

            EventRegistration registration = new EventRegistration(user, managedEvent);
            eventRegistrationRepository.save(registration);

            managedEvent.incrementAttendeesCount();
            eventRepository.save(managedEvent);
        }
    }

    // Obtiene el pedido pendiente de un usuario, si existe.
    public Optional<Order> getPendingOrder(User user) {
        List<Order> pendingOrders = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PENDING);
        return pendingOrders.isEmpty() ? Optional.empty() : Optional.of(pendingOrders.get(0));
    }

    // Recupera el pedido pendiente o crea uno nuevo.
    public Order getOrCreatePendingOrder(User user) {
        Optional<Order> orderOptional = getPendingOrder(user);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            return createOrder(user);
        }
    }

    // Obtiene todos los pedidos de un usuario.
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
