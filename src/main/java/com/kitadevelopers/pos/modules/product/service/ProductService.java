package com.kitadevelopers.pos.modules.product.service;

import com.kitadevelopers.pos.common.exception.ProductNotFoundException;
import com.kitadevelopers.pos.modules.category.entity.Category;
import com.kitadevelopers.pos.modules.category.repository.CategoryRepository;
import com.kitadevelopers.pos.modules.product.dto.CreateProductRequest;
import com.kitadevelopers.pos.modules.product.dto.ProductResponse;
import com.kitadevelopers.pos.modules.product.dto.UpdateProductRequest;
import com.kitadevelopers.pos.modules.product.entity.Product;
import com.kitadevelopers.pos.modules.product.mapper.ProductMapper;
import com.kitadevelopers.pos.modules.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
//import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;

import static com.kitadevelopers.pos.modules.product.spec.ProductSpecification.*;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository repository;
    private final CategoryRepository categoryRepository;

    public ProductResponse create(CreateProductRequest request){
        Product product = ProductMapper.toEntity(request);

        if(request.categoryId() != null){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        repository.save(product);
        return ProductMapper.toResponse(product);
    }

    public Page<ProductResponse> getAll(
            String search,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Integer minStock,
            String category,
            int page,
            int size,
            String sortBy,
            String direction
    ){
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Product> spec = Specification
                .where(notDeleted())
                .and(hasName(search))
                .and(hasMinPrice(minPrice))
                .and(hasMaxPrice(maxPrice))
                .and(hasMinStock(minStock))
                .and(hasCategory(category));

        return repository.findAll(spec, pageable)
                .map(ProductMapper::toResponse);
    }

    public ProductResponse getById(UUID id){
        Product product = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
//        Product product = repository.findById(id)
        return ProductMapper.toResponse(product);
    }

    public ProductResponse update(UUID id, UpdateProductRequest request){
        Product product = repository.findById(id)
                .orElseThrow(()-> new ProductNotFoundException(id));

        if(request.categoryId() != null){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));

            product.setCategory(category);
        }

        ProductMapper.updateEntity(product, request);
        repository.save(product);

        return ProductMapper.toResponse(product);
    }

    public void delete(UUID id){
        Product product = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setIsDeleted(true);
        product.setDeletedAt(LocalDateTime.now());

        repository.save(product);
    }
//    public void delete(UUID id){
//        if(!repository.existsById(id)){
//            throw new ProductNotFoundException(id);
//        }
//        repository.deleteById(id);
//    }

}
