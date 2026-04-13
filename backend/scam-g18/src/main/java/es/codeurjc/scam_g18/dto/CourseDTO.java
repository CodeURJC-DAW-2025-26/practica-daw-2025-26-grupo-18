package es.codeurjc.scam_g18.dto;

import es.codeurjc.scam_g18.model.Status;

public record CourseDTO(
        Long id,
        String title,
        String shortDescription,
        Integer priceCents,
        Status status,
        String language
) {}
