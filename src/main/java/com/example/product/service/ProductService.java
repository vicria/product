package com.example.product.service;

import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public void deleteProduct(UUID productId) {
        productRepository.deleteById(productId);
    }

    public Product updateProduct(UUID productId, Product updatedProduct) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setImage(updatedProduct.getImage());
        product.setCategory(updatedProduct.getCategory());
        product.setDateAdded(updatedProduct.getDateAdded());
        product.setActive(updatedProduct.isActive());

        return productRepository.save(product);
    }

    public List<Product> searchProducts(Category category, String name, Double minPrice, Double maxPrice) {
        return productRepository.findByFilters(category.getId(), name, minPrice, maxPrice);
    }

   public List<Product> findAllByIdAndCategory(Category category) {
        return productRepository.findAllByCategory(category);
   }


}
