package es.codeurjc.scam_g18.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.service.ImageService;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private ImageService imageService;

    // Serves a user's profile image or redirects to a default image.
    @GetMapping("/users/{userId}/profile")
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable Long userId) {
        return imageService.getUserImage(userId);
    }

    // Serves a course image or redirects to a default image.
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<byte[]> getCourseImage(@PathVariable Long courseId) {
        return imageService.getCourseImage(courseId);
    }

    // Serves an event image or redirects to a default image.
    @GetMapping("/events/{eventId}")
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long eventId) {
        return imageService.getEventImage(eventId);
    }
}
