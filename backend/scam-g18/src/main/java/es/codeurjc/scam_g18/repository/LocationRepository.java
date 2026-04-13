package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
