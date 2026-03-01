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
import es.codeurjc.scam_g18.model.LessonProgress;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;
import es.codeurjc.scam_g18.repository.EventRegistrationRepository;
import es.codeurjc.scam_g18.repository.LessonProgressRepository;

import java.time.LocalDateTime;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private EventRegistrationRepository eventRegistrationRepository;

    @Autowired
    private LessonProgressRepository lessonProgressRepository;

    // Retrieves a user's course enrollments.
    public List<Enrollment> findByUserId(Long userId) {
        return enrollmentRepository.findByUserId(userId);
    }

    // Returns the set of tag names from the user's courses.
    public Set<String> getTagNamesByUserId(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        return enrollments.stream()
                .flatMap(e -> e.getCourse().getTags().stream())
                .map(Tag::getName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    // Builds subscribed-course data for the profile view.
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

    // Builds user-event data for the profile view.
    public List<Map<String, Object>> getUserEvents(Long userId) {
        List<EventRegistration> registrations = eventRegistrationRepository.findByUserId(userId);
        List<Map<String, Object>> events = new ArrayList<>();
        for (EventRegistration reg : registrations) {
            Event event = reg.getEvent();
            if (event == null) {
                continue;
            }

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("eventId", event.getId());
            eventData.put("title", event.getTitle());

            String locationLabel = "Online";
            if (event.getLocation() != null) {
                String locationName = event.getLocation().getName();
                String locationCity = event.getLocation().getCity();
                if (locationName != null && !locationName.isBlank()) {
                    locationLabel = locationName;
                    if (locationCity != null && !locationCity.isBlank()) {
                        locationLabel += ", " + locationCity;
                    }
                }
            }
            eventData.put("locationLabel", locationLabel);

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

    // Retrieves names of courses completed by the user.
    public List<String> getCompletedCourseNames(Long userId) {
        List<Enrollment> completed = enrollmentRepository.findByUserIdAndProgressPercentage(userId, 100);
        return completed.stream()
                .map(e -> e.getCourse().getTitle())
                .collect(Collectors.toList());
    }

    // Counts courses currently in progress for the user.
    public int getInProgressCount(Long userId) {
        return enrollmentRepository.countByUserIdAndProgressPercentageGreaterThanAndProgressPercentageLessThan(userId,
                0, 100);
    }

    public int getAverageProgress(Long userId) {
        List<Enrollment> enrollments = enrollmentRepository.findByUserId(userId);
        if (enrollments.isEmpty())
            return 0;
        int total = 0;
        for (Enrollment e : enrollments) {
            total += e.getProgressPercentage();
        }
        return total / enrollments.size();
    }

    public long getTotalCompletedLessons(Long userId) {
        return getCompletedLessons(userId).size();
    }

    public long getLessonsCompletedThisMonth(Long userId) {
        LocalDateTime now = LocalDateTime.now();
        return getCompletedLessons(userId).stream()
                .filter(lp -> lp.getCompletedAt() != null &&
                        lp.getCompletedAt().getMonth() == now.getMonth() &&
                        lp.getCompletedAt().getYear() == now.getYear())
                .count();
    }

    public double getAverageLessonsPerMonth(Long userId) {
        List<LessonProgress> completed = getCompletedLessons(userId);
        if (completed.isEmpty())
            return 0;
        long minMonth = completed.stream()
                .map(lp -> lp.getCompletedAt())
                .filter(dt -> dt != null)
                .mapToLong(dt -> dt.getYear() * 12 + dt.getMonthValue())
                .min().orElse(0);
        long maxMonth = LocalDateTime.now().getYear() * 12 + LocalDateTime.now().getMonthValue();
        long diff = maxMonth - minMonth + 1;
        return (double) completed.size() / diff;
    }

    public int getTotalEnrollments(Long userId) {
        return (int) enrollmentRepository.countByUserId(userId);
    }

    // Returns {total, completed, remaining} enrollment counts for a user — used by
    // StatisticsController.
    public Map<String, Integer> getCourseProgressStats(Long userId) {
        int total = enrollmentRepository.findByUserId(userId).size();
        int completed = enrollmentRepository.countByUserIdAndProgressPercentage(userId, 100);
        int remaining = total - completed;
        Map<String, Integer> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("remaining", remaining);
        return stats;
    }

    // Returns the number of lessons completed per month for the last 6 months —
    // used by StatisticsController.
    public Map<String, Object> getLessonsPerMonth(Long userId) {
        List<LessonProgress> completedLessons = lessonProgressRepository.findByUserIdAndIsCompletedTrue(userId);
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 5; i >= 0; i--) {
            LocalDateTime monthDate = now.minusMonths(i);
            String monthLabel = monthDate.getMonth().getDisplayName(
                    java.time.format.TextStyle.SHORT, java.util.Locale.of("es", "ES"));
            labels.add(monthLabel);

            long count = completedLessons.stream()
                    .filter(lp -> lp.getCompletedAt() != null &&
                            lp.getCompletedAt().getMonth() == monthDate.getMonth() &&
                            lp.getCompletedAt().getYear() == monthDate.getYear())
                    .count();
            values.add((double) count);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        return result;
    }

    // Returns {completedLessons, remaining} for a specific user+course — used by
    // StatisticsController.
    public Map<String, Long> getUserLessonProgressForCourse(Long userId, Long courseId, long totalLessons) {
        long completedCount = getCompletedLessons(userId).stream()
                .filter(lp -> lp.getLesson() != null
                        && lp.getLesson().getModule() != null
                        && lp.getLesson().getModule().getCourse() != null
                        && lp.getLesson().getModule().getCourse().getId().equals(courseId))
                .count();
        long remaining = Math.max(totalLessons - completedCount, 0);
        Map<String, Long> result = new HashMap<>();
        result.put("completedLessons", completedCount);
        result.put("remaining", remaining);
        return result;
    }

    // Returns the average lessons per month formatted with one decimal digit.
    // Moves String.format("%.1f", ...) out of ProfileController.
    public String getAverageLessonsPerMonthFormatted(Long userId) {
        return String.format("%.1f", getAverageLessonsPerMonth(userId));
    }

    // Returns all completed lesson progress entries for a user — shared by multiple
    // methods.
    private List<LessonProgress> getCompletedLessons(Long userId) {
        return lessonProgressRepository.findByUserIdAndIsCompletedTrue(userId);
    }
}
