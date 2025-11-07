package com.cnpmnc.DreamCode.api.category;

import com.cnpmnc.DreamCode.dto.request.CategoryCreationRequest;
import com.cnpmnc.DreamCode.dto.request.CategoryUpdateRequest;
import com.cnpmnc.DreamCode.dto.response.CategoryResponse;
import com.cnpmnc.DreamCode.model.Category;
import com.cnpmnc.DreamCode.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Page<CategoryResponse> list(int page, int size) {
        Page<Category> categories = categoryRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        return categories.map(this::toResponse);
    }

    public CategoryResponse get(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        return toResponse(category);
    }

    public CategoryResponse create(CategoryCreationRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName().trim())) {
            throw new IllegalStateException("Category name already exists");
        }
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        Category saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    public CategoryResponse update(Integer id, CategoryUpdateRequest request) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
        
        if (request.getName() != null && StringUtils.hasText(request.getName())) {
            String newName = request.getName().trim();
            if (!newName.equalsIgnoreCase(existing.getName()) && categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new IllegalStateException("Category name already exists");
            }
            existing.setName(newName);
        }
        existing.setDescription(request.getDescription());
        Category updated = categoryRepository.save(existing);
        return toResponse(updated);
    }

    public void delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }

    // Mapper methods
    private CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}
