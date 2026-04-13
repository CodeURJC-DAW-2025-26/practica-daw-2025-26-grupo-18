package es.codeurjc.scam_g18.dto;

public record RegisterRequestDTO(
        String username,
        String email,
        String password,
        String gender,
        String birthDate,
        String country) {
}
