package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;

public record ReviewDTO(
        Long id,
        Long courseId,
        Long userId,
        String content,
        Integer rating,
        LocalDateTime createdAt) {
}
