package es.codeurjc.scam_g18.dto;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class GlobalMapper {

    public GlobalDataDTO toDTO(Map<String, Object> viewData) {
        Object userIdObj = viewData.get("userId");
        Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : null;

        return new GlobalDataDTO(
                (Boolean) viewData.getOrDefault("isUserLoggedIn", false),
                userId,
                (String) viewData.getOrDefault("userName", ""),
                (String) viewData.getOrDefault("userProfileImage", "/img/default_avatar.png"),
                (Boolean) viewData.getOrDefault("canCreateEvent", false),
                (Boolean) viewData.getOrDefault("canCreateCourse", false),
                (Boolean) viewData.getOrDefault("isAdmin", false),
                (Boolean) viewData.getOrDefault("isPublisher", false));
    }
}
