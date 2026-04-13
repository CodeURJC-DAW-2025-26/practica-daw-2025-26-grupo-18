package es.codeurjc.scam_g18.dto;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record ProfileDTO(
        Long id,
        String username,
        String email,
        String country,
        String shortDescription,
        String currentGoal,
        String weeklyRoutine,
        String comunity,
        String profileImage,
        boolean isProfileOwner,
        String userType,
        int completedCourses,
        List<String> completedCourseNames,
        int inProgressCount,
        Set<String> userTags,
        List<Map<String, Object>> subscribedCourses,
        List<Map<String, Object>> userEvents,
        int averageProgress,
        int totalEnrollments,
        long totalLessonsCompleted,
        long completedLessonsThisMonth,
        String averageLessonsPerMonth,
        List<Map<String, Object>> createdCourses,
        boolean hasMultipleCourses) {
}
