package com.example.product.repository;

import com.example.product.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Category getCategoryByName(String string);
}
