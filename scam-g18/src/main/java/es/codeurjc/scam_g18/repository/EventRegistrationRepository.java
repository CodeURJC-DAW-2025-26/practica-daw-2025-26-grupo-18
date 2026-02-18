package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByUserId(Long userId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
