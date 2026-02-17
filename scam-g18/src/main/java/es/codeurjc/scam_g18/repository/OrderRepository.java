package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
