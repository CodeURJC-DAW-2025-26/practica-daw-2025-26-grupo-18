package es.codeurjc.scam_g18.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;

@ControllerAdvice
public class GlobalControllerAdvice {

    public static class CsrfViewModel {
        private final String parameterName;
        private final String token;

        // Creates a safe model to expose CSRF parameter name and token in views.
        public CsrfViewModel(String parameterName, String token) {
            this.parameterName = (parameterName == null || parameterName.isBlank()) ? "_csrf" : parameterName;
            this.token = token == null ? "" : token;
        }

        // Returns the parameter name that must be sent in CSRF forms.
        public String getParameterName() {
            return parameterName;
        }

        // Returns the CSRF token value for forms and protected requests.
        public String getToken() {
            return token;
        }
    }

    @Autowired
    private UserService userService;

    // Adds global session, user, and CSRF attributes available in all views.
    @ModelAttribute
    public void addAttributes(Model model, HttpServletRequest request) {
        boolean hasPrincipal = (request.getUserPrincipal() != null);

        String csrfParameterName = "_csrf";
        String csrfTokenValue = "";
        Object csrfAttr = request.getAttribute("_csrf");
        if (csrfAttr instanceof CsrfToken csrfToken) {
            try {
                csrfParameterName = csrfToken.getParameterName();
                csrfTokenValue = csrfToken.getToken();
            } catch (Exception ignored) {
                csrfParameterName = "_csrf";
                csrfTokenValue = "";
            }
        } else {
            Object classAttr = request.getAttribute(CsrfToken.class.getName());
            if (classAttr instanceof CsrfToken csrfToken) {
                try {
                    csrfParameterName = csrfToken.getParameterName();
                    csrfTokenValue = csrfToken.getToken();
                } catch (Exception ignored) {
                    csrfParameterName = "_csrf";
                    csrfTokenValue = "";
                }
            }
        }
        model.addAttribute("_csrf", new CsrfViewModel(csrfParameterName, csrfTokenValue));

        // Default values always present for header rendering (Mustache fails if
        // they do not exist)
        model.addAttribute("isUserLoggedIn", false);
        model.addAttribute("userId", "");
        model.addAttribute("userName", "");
        model.addAttribute("userProfileImage", "/img/default_avatar.png");
        model.addAttribute("canCreateEvent", false);
        model.addAttribute("canCreateCourse", false);
        model.addAttribute("isAdmin", false);
        model.addAttribute("isPublisher", false);

        if (hasPrincipal) {
            var currentUser = userService.getCurrentAuthenticatedUser().orElse(null);
            // Consider the user "logged in" only if they exist in our DB
            // (PENDING Google users are not registered yet)
            if (currentUser != null) {
                userService.checkAndExpireSubscription(currentUser);

                model.addAttribute("isUserLoggedIn", true);
                model.addAttribute("userId", currentUser.getId());
                model.addAttribute("userName", currentUser.getUsername());
                model.addAttribute("userProfileImage", userService.getProfileImage(currentUser.getId()));

                boolean canCreateEvent = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("SUBSCRIBED"));
                model.addAttribute("canCreateEvent", canCreateEvent);

                boolean canCreateCourse = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("SUBSCRIBED"));
                model.addAttribute("canCreateCourse", canCreateCourse);

                boolean isAdmin = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN"));
                model.addAttribute("isAdmin", isAdmin);

                boolean isPublisher = currentUser.getRoles().stream()
                        .anyMatch(role -> role.getName().equals("ADMIN") || role.getName().equals("SUBSCRIBED"));
                model.addAttribute("isPublisher", isPublisher);
            }
        }
    }

}
