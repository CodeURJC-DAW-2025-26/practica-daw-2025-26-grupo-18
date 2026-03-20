package es.codeurjc.scam_g18.dto;

public record UserDTO(
        Long id,
        String username,
        String email,
        Boolean isActive,
        Boolean isSubscribed) {
}
