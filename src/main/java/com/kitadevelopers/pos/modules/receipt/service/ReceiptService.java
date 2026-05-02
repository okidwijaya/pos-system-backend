package com.kitadevelopers.pos.modules.receipt.service;

import com.kitadevelopers.pos.modules.order.dto.ReceiptResponse;
import com.kitadevelopers.pos.modules.order.entity.Order;
import com.kitadevelopers.pos.common.exception.ResourceNotFoundException;
import com.kitadevelopers.pos.modules.order.repository.OrderRepository;
import com.kitadevelopers.pos.modules.payment.entity.Payment;
import com.kitadevelopers.pos.modules.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReceiptService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${pos.store.name:POS Store}")
    private String storeName;

    @Value("${pos.store.address:Jl. Contoh No. 1}")
    private String storeAddress;

    @Value("${pos.store.phone:08xxxxxxxxxx}")
    private String storePhone;

    public ReceiptResponse getReceipt(UUID orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(null);

        List<ReceiptResponse.ReceiptItemLine> lines = order.getItems().stream()
                .map(item -> new ReceiptResponse.ReceiptItemLine(
                        item.getProductName(),
                        item.getProduct() != null ? item.getProduct().getSku() : "-",
                        item.getQuantity(),
                        item.getPrice(),
                        item.getTaxRate() != null ? item.getTaxRate() : BigDecimal.ZERO,
                        item.getTaxAmount() != null ? item.getTaxAmount() : BigDecimal.ZERO,
                        item.getSubtotal()
                ))
                .toList();

        BigDecimal subTotalBeforeTax = order.getTotalBeforeTax() != null
                ? order.getTotalBeforeTax()
                : order.getTotalAmount();

        BigDecimal totalTax = order.getTotalTax() != null
                ? order.getTotalTax()
                : BigDecimal.ZERO;

        return new ReceiptResponse(
                order.getId(),
                order.getOrderNumber(),
                storeName,
                storeAddress,
                storePhone,
                order.getCashier() != null ? order.getCashier().getName() : "-",
                order.getCustomer() != null ? order.getCustomer().getName() : "Walk-in Customer",
                order.getCreatedAt(),
                payment != null && payment.getMethod() != null ? payment.getMethod().name() : "N/A",
                payment != null ? payment.getStatus().name() : "PAID",
                lines,
                subTotalBeforeTax,
                totalTax,
                order.getTotalAmount(),
                order.getNotes(),
                "Terima kasih telah berbelanja"
        );
    }
}
