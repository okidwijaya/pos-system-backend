package com.kitadevelopers.pos.modules.category.repository;

import com.kitadevelopers.pos.modules.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Optional<Category> findByNameAndIsDeletedFalse(String name);
    List<Category> findAllByIsDeletedFalse();
    Optional<Category> findByIdAndIsDeletedFalse(UUID id);
//    Optional<Category> findByName(String name);
//    List<Category> findByIsDeletedFalse();
//    Optional<Category> findByIdAndIsDeletedFalse(UUID id);
//    Optional<Category> findByNameAndIsDeletedFalse(String name);
}
