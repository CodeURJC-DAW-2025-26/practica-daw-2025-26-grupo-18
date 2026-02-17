package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}