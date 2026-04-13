package es.codeurjc.scam_g18.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.Image;
import es.codeurjc.scam_g18.model.User;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.repository.EventRepository;
import es.codeurjc.scam_g18.repository.UserRepository;

import java.net.URLConnection;

@Service
public class ImageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    // Converts an uploaded file into a persistable Image entity.
    public Image saveImage(MultipartFile file) throws IOException, SQLException {
        if (file.isEmpty()) {
            return null;
        }
        Blob blob = new SerialBlob(file.getBytes());
        Image image = new Image();
        image.setData(blob);
        return image;
    }

    @Transactional
    // Returns the image in data URI format or a default image.
    public String getConnectionImage(Image image) {
        if (image != null && image.getData() != null) {
            try {
                Blob blob = image.getData();
                int blobLength = (int) blob.length();
                byte[] bytes = blob.getBytes(1, blobLength);
                String base64 = Base64.getEncoder().encodeToString(bytes);
                return "data:image/jpeg;base64," + base64;
            } catch (SQLException e) {
                e.printStackTrace();
                return "/img/default_img.png";
            }
        }
        return "/img/default_img.png";
    }

    @Transactional
    // Loads an image from a local path and transforms it into an Image entity.
    public Image saveImage(String path) throws IOException, SQLException {
        java.nio.file.Path file = java.nio.file.Paths.get("src/main/resources/static" + path);
        if (!java.nio.file.Files.exists(file)) {
            return null;
        }
        byte[] bytes = java.nio.file.Files.readAllBytes(file);
        Blob blob = new SerialBlob(bytes);
        Image image = new Image();
        image.setData(blob);
        return image;
    }

    // --- HTTP image serving (used by ImageController) ---

    @Transactional
    public ResponseEntity<byte[]> getUserImage(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return redirectToDefault("/img/default_avatar.png");
        }
        return buildImageResponse(userOpt.get().getImage(), "/img/default_avatar.png");
    }

    @Transactional
    public ResponseEntity<byte[]> getCourseImage(Long courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return redirectToDefault("/img/default_img.png");
        }
        return buildImageResponse(courseOpt.get().getImage(), "/img/default_img.png");
    }

    @Transactional
    public ResponseEntity<byte[]> getEventImage(Long eventId) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return redirectToDefault("/img/default_img.png");
        }
        return buildImageResponse(eventOpt.get().getImage(), "/img/default_img.png");
    }

    // Builds the HTTP response with image bytes and MIME type, falling back to
    // default on error.
    public ResponseEntity<byte[]> buildImageResponse(Image image, String fallbackPath) {
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

    // Redirects to a static default image.
    public ResponseEntity<byte[]> redirectToDefault(String path) {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(path));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    // Detects MIME type from raw bytes; falls back to JPEG.
    public MediaType resolveMediaType(byte[] content) {
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
