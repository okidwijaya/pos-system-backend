package com.kitadevelopers.pos.modules.category.service;

import com.kitadevelopers.pos.modules.category.dto.CategoryResponse;
import com.kitadevelopers.pos.modules.category.dto.CreateCategoryRequest;
import com.kitadevelopers.pos.modules.category.dto.UpdateCategoryRequest;
import com.kitadevelopers.pos.modules.category.entity.Category;
import com.kitadevelopers.pos.modules.category.mapper.CategoryMapper;
import com.kitadevelopers.pos.modules.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    public CategoryResponse create(CreateCategoryRequest request){

        repository.findByNameAndIsDeletedFalse(request.name())
                .ifPresent(c -> {
                    throw new RuntimeException("Category already exists");
                });

        Category category = Category.builder()
                .name(request.name())
                .description(request.description())
                .build();

        repository.save(category);

        return CategoryMapper.toResponse(category);
    }
//    public CategoryResponse create(CreateCategoryRequest request){
//        repository.findByName(request.name())
//                .ifPresent(c -> {
//                    throw new RuntimeException("Category already exists");
//                });
//
//        Category category = Category.builder()
//                .name(request.name())
//                .description(request.description())
//                .build();
//
//        repository.save(category);
//
//        return new CategoryResponse(category.getId() ,category.getName(), category.getDescription());
//    }

    public List<CategoryResponse> getAll(){
        return repository.findAllByIsDeletedFalse().stream()
                .map(c -> new CategoryResponse(c.getId(), c.getName(), c.getDescription()))
                .toList();
    }

    public CategoryResponse getById(UUID id){
        Category category = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category Not Found"));

        return CategoryMapper.toResponse(category);
    }

    public CategoryResponse update(UUID id, UpdateCategoryRequest request){
        Category category = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if(request.name() != null){
            repository.findByNameAndIsDeletedFalse(request.name())
                    .filter(c -> !c.getId().equals(id))
                    .ifPresent(c -> {
                        throw new RuntimeException("Category name already exists");
                    });
        }

        CategoryMapper.updateEntity(category, request);
        repository.save(category);

        return CategoryMapper.toResponse(category);
    }

    public void delete(UUID id){
        Category category = repository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if(Boolean.TRUE.equals(category.getIsDeleted())){
            return;
        }

        category.softDelete();
        repository.save(category);
        //        if(!repository.existsById(id)){
//            throw new RuntimeException("Category not found");
//        }
//        repository.deleteById(id);
    }
}