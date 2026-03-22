package es.codeurjc.scam_g18.controller.auth;

import java.io.IOException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.dto.RegisterRequestDTO;
import es.codeurjc.scam_g18.security.jwt.AuthResponse;
import es.codeurjc.scam_g18.security.jwt.LoginRequest;
import es.codeurjc.scam_g18.security.jwt.UserLoginService;
import es.codeurjc.scam_g18.service.EmailService;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API", description = "Registration and account availability endpoints")
public class RegisterRestController {

	private static final String REGISTER_DATA_MISSING_MESSAGE = "Datos de registro no proporcionados.";
	private static final String DUPLICATE_USER_MESSAGE = "El nombre de usuario o correo electrónico ya existen.";
	private static final String REGISTER_SUCCESS_MESSAGE = "Usuario registrado correctamente e inicio de sesión realizado.";
	private static final String REGISTER_SUCCESS_WITH_LOGIN_PENDING_MESSAGE = "Usuario registrado correctamente. Inicia sesión para continuar.";
	private static final String REGISTER_ERROR_MESSAGE = "No se pudo completar el registro.";
	private static final String REQUIRED_FIELDS_MESSAGE = "Debe indicar contraseña, fecha de nacimiento, género y país.";
	private static final String INVALID_GENDER_MESSAGE = "El género debe ser MALE, FEMALE o PREFER_NOT_TO_SAY.";

	@Autowired
	private UserService userService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserLoginService userLoginService;

	/**
	 * Checks whether the provided username and/or email are already in use.
	 */
	@GetMapping("/register/check-availability")
	@Operation(summary = "Check availability", description = "Checks whether username and email are already taken.")
	public ResponseEntity<AvailabilityResponse> checkAvailability(
			@RequestParam(required = false) String username,
			@RequestParam(required = false) String email) {

		boolean usernameTaken = userService.usernameExists(username);
		boolean emailTaken = userService.emailExists(email);

		return ResponseEntity.ok(new AvailabilityResponse(
				usernameTaken,
				emailTaken,
				!usernameTaken && !emailTaken));
	}

	/**
	 * Registers a user when the client sends JSON data.
	 */
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	@Operation(summary = "Register user (JSON)", description = "Registers a user from a JSON payload and attempts automatic login.")
	public ResponseEntity<AuthResponse> registerUserJson(@RequestBody RegisterRequestDTO request,
			HttpServletResponse response) {
		return registerUserInternal(request, null, response);
	}

	/**
	 * Registers a user when the client sends multipart data, optionally including
	 * a profile image.
	 */
	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Register user (multipart)", description = "Registers a user from multipart form data, optionally with a profile image.")
	public ResponseEntity<AuthResponse> registerUserMultipart(@ModelAttribute RegisterRequestDTO request,
			@RequestParam(value = "image", required = false) MultipartFile imageFile,
			HttpServletResponse response) {
		return registerUserInternal(request, imageFile, response);
	}

	/**
	 * Shared registration workflow: normalize input, validate attributes, create
	 * user, send welcome email, and try automatic login.
	 */
	private ResponseEntity<AuthResponse> registerUserInternal(RegisterRequestDTO request, MultipartFile imageFile,
			HttpServletResponse response) {
		if (request == null) {
			return badRequest(REGISTER_DATA_MISSING_MESSAGE);
		}

		RegisterRequestDTO data = normalizeRequest(request);

		if (missingRequiredFields(data)) {
			return badRequest(REQUIRED_FIELDS_MESSAGE);
		}

		if (!isValidGender(data.gender())) {
			return badRequest(INVALID_GENDER_MESSAGE);
		}

		String validationErrors = userService.validateUserAttributes(
				data.username(),
				data.email(),
				data.password(),
				data.birthDate(),
				data.gender(),
				data.country());
		if (validationErrors != null) {
			return badRequest(validationErrors);
		}

		try {
			boolean registered = userService.registerUser(
					data.username(),
					data.email(),
					data.password(),
					data.gender(),
					data.birthDate(),
					data.country(),
					imageFile);
			if (!registered) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new AuthResponse(AuthResponse.Status.FAILURE, DUPLICATE_USER_MESSAGE));
			}

			//in case of email delivery issues
			//we still want to consider the registration successful and allow login.
			try {
				emailService.newAccountMessage(data.email(), data.username());
			} catch (RuntimeException ex) {
				// Ignore email delivery issues
			}

			try {
				ResponseEntity<AuthResponse> loginResponse = userLoginService.login(
						response,
						new LoginRequest(data.username(), data.password()));
				AuthResponse loginBody = loginResponse.getBody();
				if (!loginResponse.getStatusCode().is2xxSuccessful()
						|| loginBody == null
						|| loginBody.getStatus() != AuthResponse.Status.SUCCESS) {
					return ResponseEntity.status(HttpStatus.CREATED)
							.body(new AuthResponse(AuthResponse.Status.SUCCESS, REGISTER_SUCCESS_WITH_LOGIN_PENDING_MESSAGE));
				}
			} catch (RuntimeException ex) {
				return ResponseEntity.status(HttpStatus.CREATED)
						.body(new AuthResponse(AuthResponse.Status.SUCCESS, REGISTER_SUCCESS_WITH_LOGIN_PENDING_MESSAGE));
			}

			return ResponseEntity.status(HttpStatus.CREATED)
					.body(new AuthResponse(AuthResponse.Status.SUCCESS, REGISTER_SUCCESS_MESSAGE));
		} catch (IOException | SQLException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(AuthResponse.Status.FAILURE, REGISTER_ERROR_MESSAGE));
		}
	}

	/**
	 * Returns a normalized copy of the incoming registration payload.
	 */
	private RegisterRequestDTO normalizeRequest(RegisterRequestDTO request) {
		return new RegisterRequestDTO(
				normalize(request.username()),
				normalize(request.email()),
				normalize(request.password()),
				normalize(request.gender()),
				normalize(request.birthDate()),
				normalize(request.country()));
	}

	/**
	 * Builds a standard 400 response body for validation or input errors.
	 */
	private ResponseEntity<AuthResponse> badRequest(String message) {
		return ResponseEntity.badRequest().body(new AuthResponse(AuthResponse.Status.FAILURE, message));
	}

	/**
	 * Trims a string and converts blank values to null.
	 */
	private static String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static boolean missingRequiredFields(RegisterRequestDTO data) {
		return data.password() == null
				|| data.birthDate() == null
				|| data.gender() == null
				|| data.country() == null;
	}

	private static boolean isValidGender(String gender) {
		return "MALE".equals(gender)
				|| "FEMALE".equals(gender)
				|| "PREFER_NOT_TO_SAY".equals(gender);
	}

	public record AvailabilityResponse(boolean usernameTaken, boolean emailTaken, boolean available) {
	}
}
