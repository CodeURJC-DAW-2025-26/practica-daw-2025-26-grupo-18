package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;
import java.util.List;
import es.codeurjc.scam_g18.model.OrderStatus;

public class OrderDTO {
    private Long id;
    private Long userId;
    private Integer totalAmountCents;
    private String totalAmountEuros;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTotalAmountCents() { return totalAmountCents; }
    public void setTotalAmountCents(Integer totalAmountCents) { this.totalAmountCents = totalAmountCents; }

    public String getTotalAmountEuros() { return totalAmountEuros; }
    public void setTotalAmountEuros(String totalAmountEuros) { this.totalAmountEuros = totalAmountEuros; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
