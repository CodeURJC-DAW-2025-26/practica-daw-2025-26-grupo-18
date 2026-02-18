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
import es.codeurjc.scam_g18.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private EnrollmentRepository enrollmentRepository;
    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

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

    // el iva de sanchez
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
            if (item.getCourse() != null) {
                enrollInCourse(order.getUser(), item.getCourse());
            }
            if (item.getEvent() != null) {
                registerForEvent(order.getUser(), item.getEvent());
            }
        }
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
