package es.codeurjc.scam_g18.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Enrollment;
import es.codeurjc.scam_g18.model.Event;
import es.codeurjc.scam_g18.model.EventRegistration;
import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.EventRegistrationRepository;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    public Set<String> getTagNamesByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .flatMap(e -> e.getCourse().getTags().stream())
                .map(Tag::getName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public List<Map<String, Object>> getSubscribedCoursesData(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        List<Map<String, Object>> courses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            Map<String, Object> courseData = new HashMap<>();
            courseData.put("courseId", enrollment.getCourse().getId());
            courseData.put("title", enrollment.getCourse().getTitle());
            courseData.put("description", enrollment.getCourse().getShortDescription());
            courseData.put("creatorName", enrollment.getCourse().getCreator() != null
                    ? enrollment.getCourse().getCreator().getUsername()
                    : "Desconocido");
            courseData.put("progress", enrollment.getProgressPercentage());
            courseData.put("isCompleted", enrollment.getProgressPercentage() == 100);

            List<String> tagNames = enrollment.getCourse().getTags().stream()
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            courseData.put("tags", tagNames);

            courses.add(courseData);
        }
        return courses;
    }

    public List<Map<String, Object>> getUserEvents(Long userId) {
        List<EventRegistration> registrations = eventRegistrationRepository.findByUserId(userId);
        List<Map<String, Object>> events = new ArrayList<>();
        for (EventRegistration reg : registrations) {
            Event event = reg.getEvent();
            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", event.getId());
            eventData.put("title", event.getTitle());
            eventData.put("locationName", event.getLocationName() != null ? event.getLocationName() : "");
            eventData.put("locationCity", event.getLocationCity() != null ? event.getLocationCity() : "");
            if (event.getStartDate() != null) {
                eventData.put("startDate", event.getStartDate().getDayOfMonth() + " "
                        + event.getStartDate().getMonth().toString().substring(0, 3));
            } else {
                eventData.put("startDate", "");
            }
            events.add(eventData);
        }
        return events;
    }

    public List<String> getCompletedCourseNames(Long userId) {
        List<Enrollment> completed = enrollmentRepository.findByUserIdAndProgressPercentage(userId, 100);
        return completed.stream()
                .map(e -> e.getCourse().getTitle())
                .collect(Collectors.toList());
    }

    public int getInProgressCount(Long userId) {
        return enrollmentRepository.countByUserIdAndProgressPercentageGreaterThanAndProgressPercentageLessThan(userId,
                0, 100);
    }
}
