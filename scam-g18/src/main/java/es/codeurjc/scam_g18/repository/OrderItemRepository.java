package es.codeurjc.scam_g18.repository;

import org.springframework.data.jpa.repository.Modifying;
import es.codeurjc.scam_g18.model.OrderItem;
import es.codeurjc.scam_g18.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Modifying
	@Query("DELETE FROM OrderItem oi WHERE oi.course.id = :courseId AND oi.order.status = :status")
	int deleteByCourseIdAndOrderStatus(@Param("courseId") Long courseId, @Param("status") OrderStatus status);

	@Modifying
	@Query("UPDATE OrderItem oi SET oi.course = NULL WHERE oi.course.id = :courseId AND oi.order.status <> :status")
	int clearCourseReferenceByCourseIdAndOrderStatusNot(@Param("courseId") Long courseId,
			@Param("status") OrderStatus status);
}
