package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;
import java.util.List;
import es.codeurjc.scam_g18.model.OrderStatus;

public record OrderDTO(
        Long id,
        Long userId,
        Integer totalAmountCents,
        String totalAmountEuros,
        OrderStatus status,
        LocalDateTime createdAt,
        List<OrderItemDTO> items
) {}
