package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.EventRegistration;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByUserId(Long userId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    @Modifying
    @Query("DELETE FROM EventRegistration er WHERE er.event.id = :eventId")
    int deleteByEventId(@Param("eventId") Long eventId);
}
