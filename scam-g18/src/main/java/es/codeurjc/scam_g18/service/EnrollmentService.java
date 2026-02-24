package es.codeurjc.scam_g18.service;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.codeurjc.scam_g18.model.Enrollment;
import es.codeurjc.scam_g18.model.Tag;
import es.codeurjc.scam_g18.repository.EnrollmentRepository;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

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
}
