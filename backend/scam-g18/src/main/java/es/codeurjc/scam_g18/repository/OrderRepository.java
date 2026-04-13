package es.codeurjc.scam_g18.repository;

import es.codeurjc.scam_g18.model.Order;
import es.codeurjc.scam_g18.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdAndStatus(Long userId, OrderStatus status);
}
