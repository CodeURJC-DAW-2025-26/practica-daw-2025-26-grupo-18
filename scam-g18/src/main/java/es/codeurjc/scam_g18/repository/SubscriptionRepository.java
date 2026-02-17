package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
