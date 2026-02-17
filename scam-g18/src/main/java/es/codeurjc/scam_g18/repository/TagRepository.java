package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}