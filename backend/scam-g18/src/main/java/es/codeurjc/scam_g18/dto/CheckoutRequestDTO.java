package es.codeurjc.scam_g18.dto;

public record CheckoutRequestDTO(
        String cardName,
        String billingEmail,
        String cardNumber,
        String cardExpiry,
        String cardCvv
) {}
