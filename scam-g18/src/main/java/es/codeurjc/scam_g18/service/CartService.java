package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import es.codeurjc.scam_g18.repository.RoleRepository;
import es.codeurjc.scam_g18.model.Role;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private OrderRepository orderRepository;
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

    private static final int SUBSCRIPTION_PRICE_CENTS = 999;
    private static final int SUBSCRIPTION_DURATION_DAYS = 30;

    // ... (lines 42-128 remain unchanged, will use separate tool call if needed or
    // just replace the method) ...
    // Wait, I can't skip lines in replacement content unless I include them.
    // I will replace the imports/fields and the activateSubscription method
    // separately or together if close.
    // They are far apart. I'll do fields first, then the method.

    // Using a new strategy: I will replace the whole file content related to fields
    // and the specific method to avoid errors,
    // but the instruction says "ReplacementChunk".
    // I will use two replace_file_content calls? No, "Do NOT make multiple parallel
    // calls to this tool".
    // I should use multi_replace_file_content.

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
    public void processPayment(Order order) {
        order.setStatus(OrderStatus.PAID);
        order.setTotalAmountCents(calculateTotal(order));
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
            EventRegistration registration = new EventRegistration(user, event);
            eventRegistrationRepository.save(registration);
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
