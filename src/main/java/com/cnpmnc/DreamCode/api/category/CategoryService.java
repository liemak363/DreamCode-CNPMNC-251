package com.cnpmnc.DreamCode.api.category;

import com.cnpmnc.DreamCode.model.Category;

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

    public Page<Category> list(int page, int size) {
        return categoryRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
    }

    public Category get(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + id));
    }

    public Category create(Category body) {
        if (body == null || !StringUtils.hasText(body.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (categoryRepository.existsByNameIgnoreCase(body.getName().trim())) {
            throw new IllegalStateException("Category name already exists");
        }
        Category c = new Category();
        c.setName(body.getName().trim());
        c.setDescription(body.getDescription());
        return categoryRepository.save(c);
    }

    public Category update(Integer id, Category body) {
        Category existing = get(id);
        if (body.getName() != null && StringUtils.hasText(body.getName())) {
            String newName = body.getName().trim();
            if (!newName.equalsIgnoreCase(existing.getName()) && categoryRepository.existsByNameIgnoreCase(newName)) {
                throw new IllegalStateException("Category name already exists");
            }
            existing.setName(newName);
        }
        existing.setDescription(body.getDescription());
        return categoryRepository.save(existing);
    }

    public void delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
