package com.kitadevelopers.pos.modules.order.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.order.dto.CheckoutRequest;
import com.kitadevelopers.pos.modules.order.dto.OrderHistoryResponse;
import com.kitadevelopers.pos.modules.order.dto.OrderResponse;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.modules.order.enums.OrderStatus;
import com.kitadevelopers.pos.modules.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService service;

    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @PostMapping("/checkout")
    public ApiResponse<OrderResponse> checkout(
            @RequestHeader("Idempotency-Key") String key,
            @RequestBody CheckoutRequest request
    ){
        return ApiResponse.success(service.checkout(request, key));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @GetMapping
    public ApiResponse<Page<OrderHistoryResponse>> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)OrderStatus status,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate startDate,
            @RequestParam(required = false)@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate endDate
            ){
        return ApiResponse.success(
                service.getOrderHistory(page, size, status, startDate, endDate)
        );
    }

//    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
//    @PostMapping
//    public ApiResponse<String> mockPay(@PathVariable UUID orderId){
//
//    }
}
//    @PostMapping
//    public ApiResponse<Order> create(@RequestParam BigDecimal total){
//        return ApiResponse.success(service.createOrder(total));
//    }
