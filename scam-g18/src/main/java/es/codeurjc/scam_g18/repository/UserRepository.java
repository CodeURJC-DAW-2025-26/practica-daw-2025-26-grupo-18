package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    java.util.List<User> findByUsernameContainingIgnoreCase(String username);
}