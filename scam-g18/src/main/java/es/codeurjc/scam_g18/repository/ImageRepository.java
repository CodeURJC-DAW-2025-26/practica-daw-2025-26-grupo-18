package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
