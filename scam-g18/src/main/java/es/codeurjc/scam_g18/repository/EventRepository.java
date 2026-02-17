package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
