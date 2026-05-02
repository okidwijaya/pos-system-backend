package com.kitadevelopers.pos.modules.category.controller;

import com.kitadevelopers.pos.common.response.ApiResponse;
import com.kitadevelopers.pos.modules.category.dto.CategoryResponse;
import com.kitadevelopers.pos.modules.category.dto.CreateCategoryRequest;
import com.kitadevelopers.pos.modules.category.dto.UpdateCategoryRequest;
import com.kitadevelopers.pos.modules.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService service;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request){
        return ApiResponse.success(service.create(request));
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> getAll(){
        return ApiResponse.success(service.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable UUID id){ return ApiResponse.success(service.getById(id)); }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ApiResponse<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody UpdateCategoryRequest request){
        return ApiResponse.success(service.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable UUID id){
        service.delete(id);
        return ApiResponse.success("Category deleted");
    }
}
