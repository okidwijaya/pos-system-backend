package com.kitadevelopers.pos.modules.product.mapper;

import com.kitadevelopers.pos.modules.product.dto.*;
import com.kitadevelopers.pos.modules.product.entity.Product;

public class ProductMapper {
//    entity
    public static Product toEntity(CreateProductRequest request){
        return Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .costPrice(request.costPrice())
                .sku(request.sku())
                .barcode(request.barcode())
                .build();
    }

    public static void updateEntity(Product product, UpdateProductRequest request){
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
    }

    public static ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory() != null
                    ? product.getCategory().getName()
                        : null
//                product.getCreatedAt(),
//                product.getUpdateAt()
        );
    }
}
