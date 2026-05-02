package com.kitadevelopers.pos.modules.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ReceiptResponse (
        UUID orderId,
        String orderNumber,

        // Store info (configure via application.yml)
        String storeName,
        String storeAddress,
        String storePhone,

        String cashierName,
        String customerName,
        LocalDateTime transactionTime,

        String paymentMethod,
        String paymentStatus,

        List<ReceiptItemLine> items,

        BigDecimal subtotalBeforeTax,
        BigDecimal totalTax,
        BigDecimal totalAmount,

        String notes,
        String footer
) {
    public record ReceiptItemLine(
            String productName,
            String sku,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal taxRate,
            BigDecimal taxAmount,
            BigDecimal subtotal
){}
}
