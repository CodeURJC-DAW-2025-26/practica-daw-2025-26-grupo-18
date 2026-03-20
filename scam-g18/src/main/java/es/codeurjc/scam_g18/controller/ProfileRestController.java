package es.codeurjc.scam_g18.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.dto.ProfileDTO;
import es.codeurjc.scam_g18.dto.ProfileUpdateRequestDTO;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.service.CourseService;
import es.codeurjc.scam_g18.service.EnrollmentService;
import es.codeurjc.scam_g18.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileRestController {

	@Autowired
	private UserService userService;

	@Autowired
	private EnrollmentService enrollmentService;

	@Autowired
	private CourseService courseService;

	@GetMapping("/me")
	public ResponseEntity<Map<String, Long>> myProfile() {
		return userService.getCurrentAuthenticatedUser()
				.map(user -> ResponseEntity.ok(Map.of("userId", user.getId())))
				.orElseGet(() -> ResponseEntity.status(401).build());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProfileDTO> profile(@PathVariable long id) {
		Optional<User> userOpt = userService.findById(id);
		if (userOpt.isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		Optional<User> currentUserOpt = userService.getCurrentAuthenticatedUser();
		boolean isProfileOwner = currentUserOpt.isPresent() && currentUserOpt.get().getId().equals(id);

		List<java.util.Map<String, Object>> createdCourses = courseService.getCreatedCoursesWithStats(id);

		User user = userOpt.get();

		ProfileDTO profileDTO = new ProfileDTO(
				user.getId(),
				user.getUsername(),
				user.getEmail(),
				user.getCountry(),
				user.getShortDescription(),
				user.getCurrentGoal(),
				user.getWeeklyRoutine(),
				user.getComunity(),
				userService.getProfileImage(id),
				isProfileOwner,
				userService.getUserType(id),
				userService.getCompletedCoursesCount(id),
				enrollmentService.getCompletedCourseNames(id),
				enrollmentService.getInProgressCount(id),
				enrollmentService.getTagNamesByUserId(id),
				enrollmentService.getSubscribedCoursesData(id),
				enrollmentService.getUserEvents(id),
				enrollmentService.getAverageProgress(id),
				enrollmentService.getTotalEnrollments(id),
				enrollmentService.getTotalCompletedLessons(id),
				enrollmentService.getLessonsCompletedThisMonth(id),
				enrollmentService.getAverageLessonsPerMonthFormatted(id),
				createdCourses,
				createdCourses.size() > 1);

		return ResponseEntity.ok(profileDTO);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> editProfileJson(
			@PathVariable long id,
			@RequestBody ProfileUpdateRequestDTO request,
			HttpServletRequest servletRequest) {
		return editProfileInternal(id, request, null, servletRequest);
	}

	@PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> editProfileMultipart(
			@PathVariable long id,
			@ModelAttribute ProfileUpdateRequestDTO request,
			@RequestParam(required = false) MultipartFile imageFile,
			HttpServletRequest servletRequest) {
		return editProfileInternal(id, request, imageFile, servletRequest);
	}

	private ResponseEntity<?> editProfileInternal(
			long id,
			ProfileUpdateRequestDTO request,
			MultipartFile imageFile,
			HttpServletRequest servletRequest) {
		if (request == null) {
			return ResponseEntity.badRequest().body(new ErrorResponse("Datos de perfil no proporcionados."));
		}

		Optional<User> currentUserOpt = userService.getCurrentAuthenticatedUser();
		if (currentUserOpt.isEmpty()) {
			return ResponseEntity.status(401).build();
		}

		User currentUser = currentUserOpt.get();
		if (!currentUser.getId().equals(id)) {
			return ResponseEntity.status(403).build();
		}

		String username = normalize(request.username());
		String email = normalize(request.email());

		String validationErrors = userService.validateUserAttributes(username, email, null, null, null, null);
		if (validationErrors != null) {
			return ResponseEntity.badRequest().body(new ErrorResponse(validationErrors));
		}

		try {
			boolean updated = userService.updateProfile(
					id,
					username,
					email,
					normalize(request.country()),
					request.shortDescription(),
					request.currentGoal(),
					request.weeklyRoutine(),
					request.comunity(),
					imageFile);

			if (!updated) {
				return ResponseEntity.notFound().build();
			}
		} catch (IOException | SQLException ex) {
			return ResponseEntity.internalServerError()
					.body(new ErrorResponse("No se pudo actualizar el perfil."));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && username != null && !auth.getName().equals(username)) {
			userService.refreshUserSession(username, servletRequest);
		}

		return ResponseEntity.noContent().build();
	}

	private static String normalize(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	public record ErrorResponse(String message) {
	}
}
