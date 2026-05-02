package com.kitadevelopers.pos.modules.category.mapper;

import com.kitadevelopers.pos.modules.category.dto.CategoryResponse;
import com.kitadevelopers.pos.modules.category.dto.UpdateCategoryRequest;
import com.kitadevelopers.pos.modules.category.entity.Category;

public class CategoryMapper {

    public static void updateEntity(Category category, UpdateCategoryRequest request){
        category.setName(request.name());
        category.setDescription(request.description());
    }

    public static CategoryResponse toResponse(Category category){
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
