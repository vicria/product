package com.example.productcatalog;

import com.example.product.entity.Category;
import com.example.product.entity.Product;
import com.example.product.repository.CategoryRepository;
import com.example.product.service.CategoryService;
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
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;
    private UUID categoryId = UUID.randomUUID();;
    private Product testProduct;

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
    void testCreateCategory() {
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.createCategory(testCategory);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        assertEquals("Electronic products", result.getDescription());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void testGetAllCategories() {
        List<Category> categories = List.of(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testUpdateCategory_Success() {
        Category updatedCategory = Category.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        Category result = categoryService.updateCategory(categoryId, updatedCategory);

        assertNotNull(result);
        assertEquals("Updated Electronics", testCategory.getName());
        assertEquals("Updated description", testCategory.getDescription());
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    void testUpdateCategory_NotFound() {
        Category updatedCategory = Category.builder()
                .name("Updated Electronics")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                categoryService.updateCategory(categoryId, updatedCategory)
        );
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testGetCategoryByName() {
        when(categoryRepository.getCategoryByName("Electronics")).thenReturn(testCategory);

        Category result = categoryService.getCategoryName("Electronics");

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
        verify(categoryRepository, times(1)).getCategoryByName("Electronics");
    }

    @Test
    void testDeleteCategory_Success() {
        List<Product> products = List.of(testProduct);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(productService.findAllByIdAndCategory(testCategory)).thenReturn(products);
        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);
        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productService, times(1)).findAllByIdAndCategory(testCategory);
        verify(productService, times(1)).createProduct(any(Product.class));
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    void testDeleteCategory_NotFound() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                categoryService.deleteCategory(categoryId)
        );
        verify(categoryRepository, times(1)).findById(categoryId);
    }

    @Test
    void testDeleteCategory_WithNoProducts() {
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(productService.findAllByIdAndCategory(testCategory)).thenReturn(List.of());
        doNothing().when(categoryRepository).deleteById(categoryId);

        categoryService.deleteCategory(categoryId);

        verify(categoryRepository, times(1)).findById(categoryId);
        verify(productService, times(1)).findAllByIdAndCategory(testCategory);
        verify(productService, never()).createProduct(any());
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }
}
