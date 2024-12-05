package app.techify.repository;

import app.techify.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    List<OrderDetail> findByOrderId(String orderId);
    
    @Query("SELECT od FROM OrderDetail od " +
           "LEFT JOIN FETCH od.product p " +
           "WHERE od.order.id = :orderId")
    List<OrderDetail> findByOrderIdWithProduct(@Param("orderId") String orderId);
}
