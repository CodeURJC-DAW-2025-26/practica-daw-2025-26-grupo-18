package es.codeurjc.scam_g18.dto;

public record ProfileUpdateRequestDTO(
        String username,
        String email,
        String country,
        String shortDescription,
        String currentGoal,
        String weeklyRoutine,
        String comunity) {
}
