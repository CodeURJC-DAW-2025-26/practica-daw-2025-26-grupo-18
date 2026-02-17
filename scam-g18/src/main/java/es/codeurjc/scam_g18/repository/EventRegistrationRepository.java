package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
}
