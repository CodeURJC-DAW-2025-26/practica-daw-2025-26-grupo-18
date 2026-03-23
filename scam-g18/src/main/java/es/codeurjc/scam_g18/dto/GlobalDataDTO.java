package es.codeurjc.scam_g18.dto;

public record GlobalDataDTO(
        boolean isUserLoggedIn,
        Long userId,
        String userName,
        String userProfileImage,
        boolean canCreateEvent,
        boolean canCreateCourse,
        boolean isAdmin,
        boolean isPublisher) {
}
