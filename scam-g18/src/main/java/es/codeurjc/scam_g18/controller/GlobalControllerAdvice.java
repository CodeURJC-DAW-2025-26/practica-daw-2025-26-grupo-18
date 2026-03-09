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

        model.addAllAttributes(userService.getGlobalHeaderViewData(hasPrincipal));
    }

}
