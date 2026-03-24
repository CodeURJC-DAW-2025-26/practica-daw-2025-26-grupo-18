package es.codeurjc.scam_g18.dto;

import java.util.List;

import es.codeurjc.scam_g18.model.Status;

public record CourseDTO(
        Long id,
        String title,
        String shortDescription,
        String longDescription,
        Double price,
        Integer priceCents,
        Double videoHours,
        Integer downloadableResources,
        List<String> learningPoints,
        List<String> prerequisites,
        List<ModuleDTO> modules,
        Status status,
        String language
) {}
