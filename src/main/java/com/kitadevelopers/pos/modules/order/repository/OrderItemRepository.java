package com.kitadevelopers.pos.modules.order.repository;

import com.kitadevelopers.pos.modules.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {
}
