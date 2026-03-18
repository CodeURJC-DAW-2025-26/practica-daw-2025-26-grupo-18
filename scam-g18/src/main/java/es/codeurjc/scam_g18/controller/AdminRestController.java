package es.codeurjc.scam_g18.controller;

import java.util.List;

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

import es.codeurjc.scam_g18.dto.CourseDTO;
import es.codeurjc.scam_g18.dto.CourseMapper;
import es.codeurjc.scam_g18.dto.EventDTO;
import es.codeurjc.scam_g18.dto.EventMapper;
import es.codeurjc.scam_g18.dto.OrderDTO;
import es.codeurjc.scam_g18.dto.OrderMapper;
import es.codeurjc.scam_g18.dto.ReviewDTO;
import es.codeurjc.scam_g18.dto.ReviewMapper;
import es.codeurjc.scam_g18.dto.UserDTO;
import es.codeurjc.scam_g18.dto.UserMapper;
import es.codeurjc.scam_g18.model.Status;
import es.codeurjc.scam_g18.service.AdminService;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRestController {

	private static final int PAGE_SIZE = 10;

	@Autowired
	private AdminService adminService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private CourseMapper courseMapper;

	@Autowired
	private EventMapper eventMapper;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private ReviewMapper reviewMapper;

	@GetMapping("/users")
	public ResponseEntity<List<UserDTO>> getUsers(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		if (query != null && !query.isBlank()) {
			return ResponseEntity.ok(userMapper.toDTOs(adminService.searchUsers(query, page, PAGE_SIZE)));
		}
		return ResponseEntity.ok(userMapper.toDTOs(adminService.getAllUsers(page, PAGE_SIZE)));
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
	public ResponseEntity<List<CourseDTO>> getCourses(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		if (query != null && !query.isBlank()) {
			return ResponseEntity.ok(courseMapper.toDTOs(adminService.searchCourses(query, page, PAGE_SIZE)));
		}
		return ResponseEntity.ok(courseMapper.toDTOs(adminService.getAllCoursesSortedByStatus(page, PAGE_SIZE)));
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
	public ResponseEntity<List<EventDTO>> getEvents(
			@RequestParam(required = false) String query,
			@RequestParam(defaultValue = "0") int page) {
		if (query != null && !query.isBlank()) {
			return ResponseEntity.ok(eventMapper.toDTOs(adminService.searchEvents(query, page, PAGE_SIZE)));
		}
		return ResponseEntity.ok(eventMapper.toDTOs(adminService.getAllEventsSortedByStatus(page, PAGE_SIZE)));
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
	public ResponseEntity<List<OrderDTO>> getOrders(@RequestParam(defaultValue = "0") int page) {
		return ResponseEntity.ok(orderMapper.toDTOs(adminService.getAllOrdersSortedByDate(page, PAGE_SIZE)));
	}

	@GetMapping("/reviews")
	public ResponseEntity<List<ReviewDTO>> getReviews() {
		return ResponseEntity.ok(reviewMapper.toDTOs(adminService.getAllReviews()));
	}

	@DeleteMapping("/reviews/{id}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
		if (!adminService.reviewExists(id)) {
			return ResponseEntity.notFound().build();
		}
		adminService.deleteReview(id);
		return ResponseEntity.noContent().build();
	}

	public record UserStatusRequest(Boolean active) {
	}

	public record ResourceStatusRequest(String status) {
	}
}
