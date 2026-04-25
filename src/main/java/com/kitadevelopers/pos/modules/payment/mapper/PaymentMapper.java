package com.kitadevelopers.pos.modules.payment.mapper;

import com.kitadevelopers.pos.modules.payment.dto.PaymentResponse;
import com.kitadevelopers.pos.modules.payment.entity.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment payment){
        return new PaymentResponse(
                payment.getId().toString(),
                payment.getOrder().getId().toString(),
                payment.getExternalId(),
                payment.getPaymentUrl(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getPaidAt()
        );
    }
}
