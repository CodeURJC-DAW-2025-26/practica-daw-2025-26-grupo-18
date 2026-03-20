package es.codeurjc.scam_g18.dto;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.security.web.csrf.CsrfToken;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class GlobalMapper {

    public GlobalDataDTO toDTO(Map<String, Object> viewData, HttpServletRequest request) {
        Object userIdObj = viewData.get("userId");
        Long userId = (userIdObj instanceof Long) ? (Long) userIdObj : null;

        String csrfParameterName = "_csrf";
        String csrfTokenValue = "";

        Object csrfAttr = request.getAttribute("_csrf");
        if (csrfAttr instanceof CsrfToken csrfToken) {
            try {
                csrfParameterName = csrfToken.getParameterName();
                csrfTokenValue = csrfToken.getToken();
            } catch (Exception ignored) {
            }
        } else {
            Object classAttr = request.getAttribute(CsrfToken.class.getName());
            if (classAttr instanceof CsrfToken csrfToken) {
                try {
                    csrfParameterName = csrfToken.getParameterName();
                    csrfTokenValue = csrfToken.getToken();
                } catch (Exception ignored) {
                }
            }
        }

        if (csrfParameterName == null || csrfParameterName.isBlank()) {
            csrfParameterName = "_csrf";
        }
        if (csrfTokenValue == null) {
            csrfTokenValue = "";
        }

        return new GlobalDataDTO(
                (Boolean) viewData.getOrDefault("isUserLoggedIn", false),
                userId,
                (String) viewData.getOrDefault("userName", ""),
                (String) viewData.getOrDefault("userProfileImage", "/img/default_avatar.png"),
                (Boolean) viewData.getOrDefault("canCreateEvent", false),
                (Boolean) viewData.getOrDefault("canCreateCourse", false),
                (Boolean) viewData.getOrDefault("isAdmin", false),
                (Boolean) viewData.getOrDefault("isPublisher", false),
                csrfParameterName,
                csrfTokenValue);
    }
}
