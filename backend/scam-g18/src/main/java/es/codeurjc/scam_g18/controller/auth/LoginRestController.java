package es.codeurjc.scam_g18.controller.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.security.jwt.AuthResponse;
import es.codeurjc.scam_g18.security.jwt.LoginRequest;
import es.codeurjc.scam_g18.security.jwt.UserLoginService;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API", description = "Authentication and token lifecycle endpoints")
public class LoginRestController {

    private final UserLoginService userLoginService;

    public LoginRestController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates a user with username and password.")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {

        if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null
                || loginRequest.getUsername().isBlank() || loginRequest.getPassword().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Username and password are required"));
        }

        return userLoginService.login(response, loginRequest);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Issues fresh credentials using a refresh token from cookie or authorization header.")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = "RefreshToken", required = false) String refreshTokenCookie,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader,
            HttpServletResponse response) {

        String refreshToken = resolveRefreshToken(refreshTokenCookie, authorizationHeader);
        if (refreshToken == null || refreshToken.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(AuthResponse.Status.FAILURE, "Refresh token is missing"));
        }

        return userLoginService.refresh(response, refreshToken);
    } 

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalidates current authentication context and clears auth cookies.")
    public ResponseEntity<AuthResponse> logout(HttpServletResponse response) {
        String message = userLoginService.logout(response);
        return ResponseEntity.ok(new AuthResponse(AuthResponse.Status.SUCCESS, message));
    }
        
    
    private String resolveRefreshToken(String refreshTokenCookie, String authorizationHeader) {
        if (refreshTokenCookie != null && !refreshTokenCookie.isBlank()) {
            return refreshTokenCookie;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }

        return null;
    } 
}
