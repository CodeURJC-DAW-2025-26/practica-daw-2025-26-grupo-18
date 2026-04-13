package es.codeurjc.scam_g18.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = true)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    private Integer priceAtPurchaseCents;

    private boolean isSubscription = false;

    public OrderItem() {
    }

    public OrderItem(Order order, Course course, Event event, Integer priceAtPurchaseCents) {
        this.order = order;
        this.course = course;
        this.event = event;
        this.priceAtPurchaseCents = priceAtPurchaseCents;
    }

    public OrderItem(Order order, Course course, Event event, Integer priceAtPurchaseCents, boolean isSubscription) {
        this.order = order;
        this.course = course;
        this.event = event;
        this.priceAtPurchaseCents = priceAtPurchaseCents;
        this.isSubscription = isSubscription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Integer getPriceAtPurchaseCents() {
        return priceAtPurchaseCents;
    }

    public String getPriceInEuros() {
        if (priceAtPurchaseCents == null) {
            return "0.00";
        }
        return String.format("%.2f", priceAtPurchaseCents / 100.0);
    }

    public void setPriceAtPurchaseCents(Integer priceAtPurchaseCents) {
        this.priceAtPurchaseCents = priceAtPurchaseCents;
    }

    public boolean isSubscription() {
        return isSubscription;
    }

    public void setSubscription(boolean isSubscription) {
        this.isSubscription = isSubscription;
    }
}
