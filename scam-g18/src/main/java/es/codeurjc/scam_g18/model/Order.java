package es.codeurjc.scam_g18.model;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private Integer totalAmountCents;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    private String paymentMethod;

    private String paymentReference;

    private String billingFullName;

    private String billingEmail;

    private String cardLast4;

    private boolean invoicePending;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items;
    
    public Order() {}

    public Order(User user, Integer totalAmountCents, OrderStatus status) {
        this.user = user;
        this.totalAmountCents = totalAmountCents;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTotalAmountCents() {
        return totalAmountCents;
    }

    public void setTotalAmountCents(Integer totalAmountCents) {
        this.totalAmountCents = totalAmountCents;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(LocalDateTime paidAt) {
        this.paidAt = paidAt;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getBillingFullName() {
        return billingFullName;
    }

    public void setBillingFullName(String billingFullName) {
        this.billingFullName = billingFullName;
    }

    public String getBillingEmail() {
        return billingEmail;
    }

    public void setBillingEmail(String billingEmail) {
        this.billingEmail = billingEmail;
    }

    public String getCardLast4() {
        return cardLast4;
    }

    public void setCardLast4(String cardLast4) {
        this.cardLast4 = cardLast4;
    }

    public boolean isInvoicePending() {
        return invoicePending;
    }

    public void setInvoicePending(boolean invoicePending) {
        this.invoicePending = invoicePending;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }
}
