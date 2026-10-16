package com.example.product.service;

import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private final ProductService productService;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        List<Product> products = productService.findAllByIdAndCategory(category);
        for (Product product : products) {
            product.setActive(true);
            product.setCategory(null);
            productService.createProduct(product);
        }
        categoryRepository.deleteById(categoryId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }


    public Category updateCategory(UUID categoryId, Category updatedCategory) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.setName(updatedCategory.getName());
        category.setDescription(updatedCategory.getDescription());
        return categoryRepository.save(category);
    }

    public Category getCategoryName(String name) {
        return categoryRepository.getCategoryByName(name);
    }
}
