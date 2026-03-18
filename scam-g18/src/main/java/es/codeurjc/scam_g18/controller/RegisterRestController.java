package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;

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

@RestController
@RequestMapping("/api/v1")
public class RegisterRestController {

	private final UserService userService;
	private final EmailService emailService;
	private final UserLoginService userLoginService;

	public RegisterRestController(UserService userService, EmailService emailService, UserLoginService userLoginService) {
		this.userService = userService;
		this.emailService = emailService;
		this.userLoginService = userLoginService;
	}

	@GetMapping("/register/check-availability")
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

	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AuthResponse> registerUserJson(@RequestBody RegisterRequestDTO request,
			HttpServletResponse response) {
		return registerUserInternal(request, null, response);
	}

	@PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<AuthResponse> registerUserMultipart(@ModelAttribute RegisterRequestDTO request,
			@RequestParam(value = "image", required = false) MultipartFile imageFile,
			HttpServletResponse response) {
		return registerUserInternal(request, imageFile, response);
	}

	private ResponseEntity<AuthResponse> registerUserInternal(RegisterRequestDTO request, MultipartFile imageFile,
			HttpServletResponse response) {
		if (request == null) {
			return badRequest("Datos de registro no proporcionados.");
		}

		String username = normalize(request.username());
		String email = normalize(request.email());
		String password = normalize(request.password());
		String gender = normalize(request.gender());
		String birthDate = normalize(request.birthDate());
		String country = normalize(request.country());

		String requiredFieldsError = requiredFieldsError(password, birthDate, gender, country);
		if (requiredFieldsError != null) {
			return badRequest(requiredFieldsError);
		}

		String validationErrors = userService.validateUserAttributes(username, email, password, birthDate, gender, country);
		if (validationErrors != null) {
			return badRequest(validationErrors);
		}

		try {
			boolean registered = userService.registerUser(username, email, password, gender, birthDate, country,
					imageFile);
			if (!registered) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body(new AuthResponse(AuthResponse.Status.FAILURE,
								"El nombre de usuario o correo electrónico ya existen."));
			}

			emailService.newAccountMessage(email, username);
			
			userLoginService.login(response, new LoginRequest(username, password));
				return ResponseEntity.status(HttpStatus.CREATED)
						.body(new AuthResponse(AuthResponse.Status.SUCCESS,
								"Usuario registrado correctamente e inicio de sesión realizado."));
			
		} catch (IOException | SQLException ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new AuthResponse(AuthResponse.Status.FAILURE,
							"No se pudo completar el registro."));
		}
	}

	private ResponseEntity<AuthResponse> badRequest(String message) {
		return ResponseEntity.badRequest().body(new AuthResponse(AuthResponse.Status.FAILURE, message));
	}

	private static String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private static String requiredFieldsError(String password, String birthDate, String gender, String country) {
		if (password == null) {
			return "Por favor, introduzca una contraseña.";
		}
		if (birthDate == null) {
			return "Por favor, introduzca una fecha de nacimiento.";
		}
		if (gender == null) {
			return "Por favor, seleccione un género.";
		}
		if (country == null) {
			return "Por favor, seleccione un país.";
		}
		return null;
	}

	public record AvailabilityResponse(boolean usernameTaken, boolean emailTaken, boolean available) {
	}
}
