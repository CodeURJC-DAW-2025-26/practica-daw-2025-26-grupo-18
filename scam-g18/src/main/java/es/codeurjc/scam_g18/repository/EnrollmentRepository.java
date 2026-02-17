package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
