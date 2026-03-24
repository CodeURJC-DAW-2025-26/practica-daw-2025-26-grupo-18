package es.codeurjc.scam_g18.dto;

import java.time.LocalDateTime;
import java.util.List;

import es.codeurjc.scam_g18.model.Status;

public record EventDTO(
        Long id,
        String title,
        String description,
        Double price,
        Integer priceCents,
        LocalDateTime startDate,
        LocalDateTime endDate,
        String startDateStr,
        String startTimeStr,
        String endDateStr,
        String endTimeStr,
        Integer capacity,
        String category,
        String locationName,
        String locationAddress,
        String locationCity,
        String locationCountry,
        Double locationLatitude,
        Double locationLongitude,
        List<String> speakerNames,
        List<String> sessionTimes,
        List<String> sessionTitles,
        List<String> sessionDescriptions,
        Status status
) {}
