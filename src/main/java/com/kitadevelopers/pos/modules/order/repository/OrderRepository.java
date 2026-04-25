package com.kitadevelopers.pos.modules.order.repository;

import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID>{
    @Query("""
SELECT o FROM Order o
WHERE o.cashier.id = :cashierId
AND o.createdAt BETWEEN :startDate AND :endDate
AND (:status IS NULL OR o.orderStatus = :status)
""")
    Page<Order> findOrdersWithFilter(
            @Param("cashierId") UUID cashierId,
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    Optional<Order> findByIdempotencyKey (String key);
}
//public interface OrderRepository extends JpaRepository<Order, UUID>, JpaSpecificationExecutor<Order> {
//}

//@Query("""
//    SELECT o FROM Order o
//    WHERE o.cashier.id = :cashierId
//    AND (:status IS NULL OR o.orderStatus = :status)
//    AND (:startDate IS NULL OR o.createdAt >= :startDate)
//    AND (:endDate IS NULL OR o.createdAt <= :endDate)
//""")

//@Query("""
//SELECT o FROM Order o
//WHERE o.cashier.id = :cashierId
//AND o.orderStatus = :status
//AND o.createdAt BETWEEN :startDate AND :endDate
//""")