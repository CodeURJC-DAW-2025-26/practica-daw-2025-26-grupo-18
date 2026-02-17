package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
