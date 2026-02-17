package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
