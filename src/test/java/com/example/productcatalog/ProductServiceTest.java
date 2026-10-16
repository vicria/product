package com.example.productcatalog;
import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.repository.ProductRepository;
import com.example.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;
    private UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {

        testCategory = Category.builder()
                .name("Electronics")
                .description("Electronic products")
                .build();

        testProduct = Product.builder()
                .name("Laptop")
                .description("High performance laptop")
                .price(999.99)
                .image("laptop.jpg")
                .category(testCategory)
                .dateAdded(LocalDate.now())
                .active(true)
                .build();
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.createProduct(testProduct);

        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals(999.99, result.getPrice());
        assertEquals(testCategory, result.getCategory());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testDeleteProduct() {
        productService.deleteProduct(productId);

        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    void testUpdateProduct_Success() {
        Product updatedProduct = Product.builder()
                .name("Gaming Laptop")
                .description("Updated description")
                .price(1299.99)
                .category(testCategory)
                .dateAdded(LocalDate.now())
                .active(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        Product result = productService.updateProduct(productId, updatedProduct);

        assertNotNull(result);
        assertEquals("Gaming Laptop", testProduct.getName());
        assertEquals(1299.99, testProduct.getPrice());
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void testUpdateProduct_NotFound() {
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                productService.updateProduct(productId, testProduct)
        );
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testSearchProducts() {
        List<Product> products = List.of(testProduct);
        when(productRepository.findByFilters(testCategory.getId(), "Laptop", 500.0, 1500.0))
                .thenReturn(products);

        List<Product> result = productService.searchProducts(testCategory, "Laptop", 500.0, 1500.0);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(productRepository, times(1)).findByFilters(testCategory.getId(), "Laptop", 500.0, 1500.0);
    }

    @Test
    void testFindAllByIdAndCategory() {
        List<Product> products = List.of(testProduct);
        when(productRepository.findAllByCategory(testCategory)).thenReturn(products);

        List<Product> result = productService.findAllByIdAndCategory(testCategory);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0).getCategory());
        verify(productRepository, times(1)).findAllByCategory(testCategory);
    }
}
