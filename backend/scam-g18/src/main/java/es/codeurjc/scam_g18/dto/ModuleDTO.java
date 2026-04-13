package es.codeurjc.scam_g18.dto;

import java.util.List;

public record ModuleDTO(
        Long id,
        String title,
        String description,
        Integer orderIndex,
        List<LessonDTO> lessons
) {}
