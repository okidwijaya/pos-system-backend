package com.kitadevelopers.pos.modules.product.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse (
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stock,
        String category
){
}
//        LocalDateTime createdAt,
//        LocalDateTime updateAt
