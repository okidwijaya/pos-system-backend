package com.kitadevelopers.pos.modules.cart.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.cart.dto.AddToCartRequest;
import com.kitadevelopers.pos.modules.cart.dto.CartResponse;
import com.kitadevelopers.pos.modules.cart.dto.UpdateCartItemRequest;
import com.kitadevelopers.pos.modules.cart.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PreAuthorize("hasRole('CASHIER')")
    @PostMapping("/items")
    public ApiResponse<CartResponse> add(@Valid @RequestBody AddToCartRequest request) {
        return ApiResponse.success(service.addToCart(request));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @PostMapping
    public ApiResponse<CartResponse> getMyCart(){
        return ApiResponse.success(service.getMyCart());
    }

    @PreAuthorize("hasRole('CASHIER')")
    @GetMapping("/{id}")
    public ApiResponse<CartResponse> getById(@PathVariable UUID id){
        return ApiResponse.success(service.getById(id));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @PutMapping("/item")
    public ApiResponse<CartResponse> updateItem(@Valid @RequestBody UpdateCartItemRequest request){
        return ApiResponse.success(service.updateItem(request));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @DeleteMapping("/item/{id}")
    public ApiResponse<CartResponse> removeItem(@PathVariable UUID id){
        return ApiResponse.success(service.removeItem(id));
    }

    @PreAuthorize("hasRole('CASHIER')")
    @DeleteMapping("/clear")
    public ApiResponse<String> clear(){
        service.clearCart();
        return ApiResponse.success("Cart cleared");
    }

    @PreAuthorize("hasRole('CASHIER')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteCart(@PathVariable UUID id){
        service.deleteCart(id);
        return ApiResponse.success("Cart deleted");
    }
}
//    @PostMapping("/checkout")
//    public ApiResponse<Order> checkout(@RequestBody CheckoutRequest request){
//        return ApiResponse.success(service.checkout(request));
//    }
