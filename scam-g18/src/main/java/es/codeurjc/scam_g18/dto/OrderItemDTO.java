package es.codeurjc.scam_g18.dto;

public record OrderItemDTO(
        Long id,
        CourseDTO course,
        EventDTO event,
        Integer priceAtPurchaseCents,
        String priceInEuros,
        boolean subscription
) {}
