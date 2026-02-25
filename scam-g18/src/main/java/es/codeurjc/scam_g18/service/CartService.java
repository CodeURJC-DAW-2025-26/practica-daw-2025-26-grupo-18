package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.util.List;
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

    private static final int SUBSCRIPTION_PRICE_CENTS = 999;
    private static final int SUBSCRIPTION_DURATION_DAYS = 30;

    public Order createOrder(User user) {
        Order order = new Order(user, 0, OrderStatus.PENDING);
        order.setItems(new ArrayList<>());
        return orderRepository.save(order);
    }

    public void addCourseToOrder(Order order, Course course) {
        OrderItem item = new OrderItem(order, course, null, course.getPriceCents());
        order.getItems().add(item);
        order.setTotalAmountCents(calculateSubtotal(order));
        orderRepository.save(order);
    }

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

    public int calculateTax(Order order) {
        return (int) (calculateSubtotal(order) * 0.21);
    }

    public int calculateTotal(Order order) {
        return calculateSubtotal(order) + calculateTax(order);
    }

    public String formatPriceInEuros(int cents) {
        return String.format("%.2f", cents / 100.0);
    }

    @Transactional
    public void processPayment(Order order, String cardName, String billingEmail, String cardNumber,
            String cardExpiry) {
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

    private String generatePaymentReference() {
        return "SIM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

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

    private void activateSubscription(User user) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByUserIdAndStatus(user.getId(),
                SubscriptionStatus.ACTIVE);

        Subscription subscription;
        if (existingSubscription.isPresent()) {
            subscription = existingSubscription.get();
            if (subscription.getEndDate().isAfter(java.time.LocalDateTime.now())) {
                subscription.setEndDate(subscription.getEndDate().plusDays(SUBSCRIPTION_DURATION_DAYS));
            } else {
                subscription.setEndDate(java.time.LocalDateTime.now().plusDays(SUBSCRIPTION_DURATION_DAYS));
            }
        } else {
            subscription = new Subscription(user, java.time.LocalDateTime.now(),
                    java.time.LocalDateTime.now().plusDays(SUBSCRIPTION_DURATION_DAYS), SubscriptionStatus.ACTIVE);

            // Actualizar rol de usuario a SUBSCRIBED si no lo tiene
            Role subscribedRole = roleRepository.findByName("SUBSCRIBED").orElse(null);
            if (subscribedRole != null) {
                boolean hasRole = user.getRoles().stream().anyMatch(r -> r.getName().equals("SUBSCRIBED"));
                if (!hasRole) {
                    user.getRoles().add(subscribedRole);
                    userService.save(user);
                }
            }
        }
        subscriptionRepository.save(subscription);
    }

    private void enrollInCourse(User user, Course course) {
        if (!enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            Enrollment enrollment = new Enrollment(user, course);
            enrollmentRepository.save(enrollment);
        }
    }

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

    public Optional<Order> getPendingOrder(User user) {
        List<Order> pendingOrders = orderRepository.findByUserIdAndStatus(user.getId(), OrderStatus.PENDING);
        return pendingOrders.isEmpty() ? Optional.empty() : Optional.of(pendingOrders.get(0));
    }

    public Order getOrCreatePendingOrder(User user) {
        Optional<Order> orderOptional = getPendingOrder(user);
        if (orderOptional.isPresent()) {
            return orderOptional.get();
        } else {
            return createOrder(user);
        }
    }

    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
