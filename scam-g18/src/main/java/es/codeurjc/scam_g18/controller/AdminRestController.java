package es.codeurjc.scam_g18.controller;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.model.Review;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

	private static final int PAGE_SIZE = 10;
	private static final DateTimeFormatter REVIEW_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

	@Autowired
	private AdminService adminService;

	@GetMapping("/users")
	public ResponseEntity<List<Map<String, Object>>> getUsers(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(adminService.getUsersApiData(query, page, PAGE_SIZE));
	}

	@PutMapping("/users/{id}/status")
	public ResponseEntity<Void> updateUserStatus(
			@PathVariable Long id,
			@RequestBody UserStatusRequest request) {
		if (request == null || request.active() == null) {
			return ResponseEntity.badRequest().build();
		}

		if (adminService.getUserById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}

		if (request.active()) {
			adminService.unbanUser(id);
		} else {
			adminService.banUser(id);
		}

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/courses")
	public ResponseEntity<List<Map<String, Object>>> getCourses(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(adminService.getCoursesApiData(query, page, PAGE_SIZE));
	}

	@PutMapping("/courses/{id}/status")
	public ResponseEntity<Void> updateCourseStatus(
			@PathVariable Long id,
			@RequestBody ResourceStatusRequest request) {
		if (request == null || request.status() == null || request.status().isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		if (!adminService.courseExists(id)) {
			return ResponseEntity.notFound().build();
		}

		Status status;
		try {
			status = Status.valueOf(request.status().trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}

		if (status == Status.PUBLISHED) {
			adminService.approveCourse(id);
		} else if (status == Status.DRAFT) {
			adminService.rejectCourse(id);
		} else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/events")
	public ResponseEntity<List<Map<String, Object>>> getEvents(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(adminService.getEventsApiData(query, page, PAGE_SIZE));
	}

	@PutMapping("/events/{id}/status")
	public ResponseEntity<Void> updateEventStatus(
			@PathVariable Long id,
			@RequestBody ResourceStatusRequest request) {
		if (request == null || request.status() == null || request.status().isBlank()) {
			return ResponseEntity.badRequest().build();
		}

		if (!adminService.eventExists(id)) {
			return ResponseEntity.notFound().build();
		}

		Status status;
		try {
			status = Status.valueOf(request.status().trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}

		if (status == Status.PUBLISHED) {
			adminService.approveEvent(id);
		} else if (status == Status.DRAFT) {
			adminService.rejectEvent(id);
		} else {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.noContent().build();
	}

	@GetMapping("/orders")
	public ResponseEntity<List<Map<String, Object>>> getOrders(@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(adminService.getOrdersApiData(page, PAGE_SIZE));
	}

	@GetMapping("/reviews")
	public ResponseEntity<List<Map<String, Object>>> getReviews() {
		List<Map<String, Object>> payload = adminService.getAllReviews().stream()
				.map(this::reviewToMap)
				.toList();
		return ResponseEntity.ok(payload);
	}

	@DeleteMapping("/reviews/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		if (!adminService.reviewExists(id)) {
			return ResponseEntity.notFound().build();
		}
		adminService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}

	private Map<String, Object> reviewToMap(Review review) {
		Map<String, Object> map = new HashMap<>();
		map.put("id", review.getId());
		map.put("content", review.getContent());
		map.put("rating", review.getRating());
		map.put("username", review.getUser() != null ? review.getUser().getUsername() : "");
		map.put("courseTitle", review.getCourse() != null ? review.getCourse().getTitle() : "");
		map.put("createdAt", review.getCreatedAt() != null ? review.getCreatedAt().format(REVIEW_DATE_FORMAT) : "");
		return map;
	}

	public record UserStatusRequest(Boolean active) {
	}

	public record ResourceStatusRequest(String status) {
	}
}
