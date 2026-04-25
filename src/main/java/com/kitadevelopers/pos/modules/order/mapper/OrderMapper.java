package com.kitadevelopers.pos.modules.order.mapper;

import com.kitadevelopers.pos.modules.order.dto.OrderHistoryResponse;
import com.kitadevelopers.pos.modules.order.dto.OrderResponse;
import com.kitadevelopers.pos.modules.order.dto.OrderResponseItem;
import com.kitadevelopers.pos.modules.order.entity.Order;

import java.util.List;

public class OrderMapper {

    public static OrderResponse toResponse(Order order){
        List<OrderResponseItem> items = order.getItems()
                .stream()
                .map(item -> new OrderResponseItem(
                        item.getProductName(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getSubtotal()
                ))
                .toList();
        return new OrderResponse(
                order.getOrderNumber(),
                order.getCashier().getName(),
                order.getCustomer() != null ? order.getCustomer().getName(): null,
                order.getTotalAmount(),
                items
        );
    }

    public static OrderHistoryResponse toHistoryRespnoe(Order order){
        return new OrderHistoryResponse(
                order.getId().toString(),
                order.getOrderNumber(),
                order.getCashier().getName(),
                order.getCustomer() != null ? order.getCustomer().getName() : null,
                order.getTotalAmount(),
                order.getOrderStatus().name(),
                order.getCreatedAt()
        );
    }
}
