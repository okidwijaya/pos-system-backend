package com.kitadevelopers.pos.modules.product.spec;

import com.kitadevelopers.pos.modules.product.entity.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {
    public static Specification<Product> hasName(String name){
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasMinPrice(BigDecimal minPrice){
        return ((root, query, cb) ->
                minPrice == null ? null :
                cb.greaterThanOrEqualTo(root.get("Price"), minPrice));
    }

    public static Specification<Product> hasMaxPrice(BigDecimal maxPrice){
        return ((root, query, cb) ->
                maxPrice == null ? null :
                        cb.lessThanOrEqualTo(root.get("Price"), maxPrice));
    }

    public static Specification<Product> hasMinStock(Integer minStock){
        return ((root, query, cb) ->
                minStock == null ? null :
                        cb.greaterThanOrEqualTo(root.get("Stock"), minStock));
    }

    public static Specification<Product> hasCategory(String categoryName){
        return (root, query,cb) -> {
            if(categoryName == null) return null;

            Join<Object, Object> category = root.join("category");

            return cb.equal(
                    cb.lower(category.get("name")),
                    categoryName.toLowerCase()
            );
        };
    }
}
