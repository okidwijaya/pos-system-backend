package com.kitadevelopers.pos.modules.product.repository;

import com.kitadevelopers.pos.modules.product.entity.Product;
//import org.springframework.data.domain.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>,  JpaSpecificationExecutor<Product>{
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") UUID id);
}
//    Page<Product> findByNameContainingIgnoreCase(
//            String name,
//            Pageable pageable//
//    );
//
//    Page<Product> findByPriceBetween(
//            BigDecimal min,
//            BigDecimal max,
//            Pageable pageable
//    );
//
//    Page<Product> findByStockGreaterThanEqual(
//            Integer Stock,
//            Pageable pageable
//    );
