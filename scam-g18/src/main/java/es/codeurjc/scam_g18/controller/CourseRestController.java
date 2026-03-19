package es.codeurjc.scam_g18.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import es.codeurjc.scam_g18.dto.CourseDTO;
import es.codeurjc.scam_g18.dto.CourseMapper;
import es.codeurjc.scam_g18.model.Course;
import es.codeurjc.scam_g18.repository.CourseRepository;
import es.codeurjc.scam_g18.service.CourseService;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseRestController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/")
    public ResponseEntity<List<CourseDTO>> getCourses() {
        return ResponseEntity.ok(courseMapper.toDTOs(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourse(@PathVariable long id) {
        try {
            Course course = courseService.getCourseById(id);
            return ResponseEntity.ok(courseMapper.toDTO(course));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/")
    public ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO courseDTO) {
        Course course = courseMapper.toDomain(courseDTO);
        courseRepository.save(course);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(course.getId()).toUri();
        return ResponseEntity.created(location).body(courseMapper.toDTO(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable long id, @RequestBody CourseDTO courseDTO) {
        try {
            courseService.getCourseById(id);
            Course updatedCourse = courseMapper.toDomain(courseDTO);
            updatedCourse.setId(id);
            courseRepository.save(updatedCourse);
            return ResponseEntity.ok(courseMapper.toDTO(updatedCourse));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable long id) {
        try {
            Course course = courseService.getCourseById(id);
            courseRepository.delete(course);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
