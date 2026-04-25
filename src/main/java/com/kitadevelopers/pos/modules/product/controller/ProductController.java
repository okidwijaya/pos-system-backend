package com.kitadevelopers.pos.modules.product.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.product.dto.CreateProductRequest;
import com.kitadevelopers.pos.modules.product.dto.ProductResponse;
import com.kitadevelopers.pos.modules.product.dto.UpdateProductRequest;
//import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
//import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService service;

    @PostMapping
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request){
        return service.create(request);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @GetMapping
    public ApiResponse<Page<ProductResponse>> getAll(
            @RequestParam(required = false) String search,

            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,

            @RequestParam(required = false) Integer minStock,
            @RequestParam(required = false) String category,

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,

            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
            )
    {
        return ApiResponse.success(
                service.getAll(search, minPrice, maxPrice, minStock, category, page, size, sortBy, direction)
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CASHIER')")
    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable UUID id){
        return service.getById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id,@Valid @RequestBody UpdateProductRequest request){
        return service.update(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable UUID id){
        service.delete(id);
        return  "Deleted Successfully";
    }
}
