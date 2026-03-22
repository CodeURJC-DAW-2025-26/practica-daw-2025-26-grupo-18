package es.codeurjc.scam_g18.security.jwt;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class UnauthorizedHandlerJwt implements AuthenticationEntryPoint {

  private static final Logger logger = LoggerFactory.getLogger(UnauthorizedHandlerJwt.class);

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException {
    logger.info("Unauthorized error: {}", authException.getMessage());

    String path = request.getServletPath();
    
    if (path.startsWith("/api/v1")) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(java.util.Map.of(
          "status", HttpServletResponse.SC_UNAUTHORIZED,
          "message", "No autorizado",
          "path", path,
          "error", authException.getMessage()
      ));
      
      response.getWriter().write(json);
    } else {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "message: %s, path: %s".formatted(authException.getMessage(), path));
    }
  }
}