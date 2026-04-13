package es.codeurjc.scam_g18.dto;

public record LessonDTO(
        Long id,
        String title,
        String videoUrl,
        String description,
        Integer orderIndex
) {}
