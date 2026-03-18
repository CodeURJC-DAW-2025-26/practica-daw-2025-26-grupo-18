package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;
import es.codeurjc.scam_g18.model.Status;

public record EventDTO(
        Long id,
        String title,
        String description,
        Integer priceCents,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer capacity,
        String category,
        Status status
) {}
