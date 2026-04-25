package com.kitadevelopers.pos.modules.cart.mapper;

import com.kitadevelopers.pos.modules.cart.dto.CartItemResponse;
import com.kitadevelopers.pos.modules.cart.dto.CartResponse;
import com.kitadevelopers.pos.modules.cart.entity.Cart;

import java.math.BigDecimal;

public class CartMapper {

    public static CartResponse toResponse(Cart cart){

        return new CartResponse(
                cart.getId().toString(),
                cart.getItems().stream()
                        .map(item -> {
                            BigDecimal subtotal = item.getPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                            // add this subtotal(add in dto cause custom response without data in db)
                            // and update dto cartresponse and add the return, with no this u can just add new
                            return new CartItemResponse(
                                    item.getProduct().getName(),
                                    item.getQuantity(),
                                    item.getPrice(),
                                    subtotal
                            );
                        })
                        .toList(),
                cart.getTotal()
        );
    }
}
