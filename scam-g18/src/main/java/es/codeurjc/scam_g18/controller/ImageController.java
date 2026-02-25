package es.codeurjc.scam_g18.controller;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Image;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.UserRepository;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/images")
public class ImageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/users/{userId}/profile")
    @Transactional
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return redirectToDefault("/img/default_avatar.png");
        }
        return buildImageResponse(userOpt.get().getImage(), "/img/default_avatar.png");
    }

    @GetMapping("/courses/{courseId}")
    @Transactional
    public ResponseEntity<byte[]> getCourseImage(@PathVariable Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return redirectToDefault("/img/default_img.png");
        }
        return buildImageResponse(courseOpt.get().getImage(), "/img/default_img.png");
    }

    @GetMapping("/events/{eventId}")
    @Transactional
    public ResponseEntity<byte[]> getEventImage(@PathVariable Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return redirectToDefault("/img/default_img.png");
        }
        return buildImageResponse(eventOpt.get().getImage(), "/img/default_img.png");
    }

    private ResponseEntity<byte[]> buildImageResponse(Image image, String fallbackPath) {
        if (image == null || image.getData() == null) {
            return redirectToDefault(fallbackPath);
        }

        try {
            Blob blob = image.getData();
            byte[] content = blob.getBytes(1, (int) blob.length());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(resolveMediaType(content));
            headers.setCacheControl(CacheControl.noCache().getHeaderValue());

            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (SQLException e) {
            return redirectToDefault(fallbackPath);
        }
    }

    private ResponseEntity<byte[]> redirectToDefault(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(path));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private MediaType resolveMediaType(byte[] content) {
        try {
            String guessedType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(content));
            if (guessedType != null && !guessedType.isBlank()) {
                return MediaType.parseMediaType(guessedType);
            }
        } catch (Exception ignored) {
        }
        return MediaType.IMAGE_JPEG;
    }
}
