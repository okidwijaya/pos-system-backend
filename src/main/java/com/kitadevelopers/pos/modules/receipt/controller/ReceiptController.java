package com.kitadevelopers.pos.modules.receipt.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.order.dto.ReceiptResponse;
import com.kitadevelopers.pos.modules.receipt.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @GetMapping("/{orderId}")
    public ApiResponse<ReceiptResponse> getReceipt(@PathVariable UUID orderId){
        return ApiResponse.success(receiptService.getReceipt(orderId));
    }
}
