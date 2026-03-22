package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/images")
@Tag(name = "Image API", description = "Endpoints that serve profile, course and event images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    // Serves a user's profile image or redirects to a default image.
    @GetMapping("/users/{userId}/profile")
    @Operation(summary = "Get user profile image", description = "Returns profile image bytes for a user id.")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable Long userId) {
        return imageService.getUserImage(userId);
    }

    // Serves a course image or redirects to a default image.
    @GetMapping("/courses/{courseId}")
    @Operation(summary = "Get course image", description = "Returns course image bytes for a course id.")
    public ResponseEntity<byte[]> getCourseImage(@PathVariable Long courseId) {
        return imageService.getCourseImage(courseId);
    }

    // Serves an event image or redirects to a default image.
    @GetMapping("/events/{eventId}")
    @Operation(summary = "Get event image", description = "Returns event image bytes for an event id.")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long eventId) {
        return imageService.getEventImage(eventId);
    }
}
