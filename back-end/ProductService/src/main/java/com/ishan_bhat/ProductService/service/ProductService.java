
package com.ishan_bhat.ProductService.service;

import com.ishan_bhat.ProductService.dto.ProductDto;
import com.ishan_bhat.ProductService.entity.Category;
import com.ishan_bhat.ProductService.entity.Product;
import com.ishan_bhat.ProductService.exception.DataAlreadyExistsException; // Import DataAlreadyExistsException
import com.ishan_bhat.ProductService.exception.ResourceNotFoundException;
import com.ishan_bhat.ProductService.repository.CategoryRepository; // Import CategoryRepository
import com.ishan_bhat.ProductService.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository; // Inject CategoryRepository

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) { // Update constructor
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ProductDto createProduct(ProductDto productDto) {
        // Category Validation
        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDto.getCategoryId()));

            if (productDto.getCategoryName() != null && !category.getName().equals(productDto.getCategoryName())) {
                throw new DataAlreadyExistsException("Category name does not match the category id provided."); // Or a more specific exception
            }
        } else if (productDto.getCategoryName() != null) {
            throw new IllegalArgumentException("CategoryId is required when CategoryName is provided."); // Or handle as needed, maybe create category if not exists
        }


        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return getProductById(savedProduct.getId());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        System.out.println("Product Name from Entity in ProductService: " + product.getName());
        System.out.println("Product Images from Entity in ProductService: " + product.getImagesUrl());
        return convertToDto(product);
    }

    public List<ProductDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ProductDto updateProduct(Long id, ProductDto productDtoDetails) {
        Product productEntity = getProductByIdEntity(id);

        if (productDtoDetails.getName() != null) {
            productEntity.setName(productDtoDetails.getName());
        }
        if (productDtoDetails.getDescription() != null) {
            productEntity.setDescription(productDtoDetails.getDescription());
        }
        if (productDtoDetails.getImagesUrl() != null) {
            productEntity.setImagesUrl(productDtoDetails.getImagesUrl());
        }
        if (productDtoDetails.getCategoryId() != null) {
            productEntity.setCategoryId(productDtoDetails.getCategoryId());
        }

        Product updatedProduct = productRepository.save(productEntity);
        return convertToDto(updatedProduct);
    }

    public void deleteProduct(Long id) {
        Product product = getProductByIdEntity(id);
        productRepository.delete(product);
    }

    private ProductDto convertToDto(Product product) {
        String categoryName = null;
        if (product.getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategoryId()).orElse(null); // Fetch category to get name
            if (category != null) {
                categoryName = category.getName();
            }
        }

        ProductDto productDto = new ProductDto(
                product.getId(),
                product.getCategoryId(),
                categoryName, // Set category name in Dto
                product.getName(),
                product.getDescription(),
                product.getImagesUrl()
        );
        System.out.println("ProductDto being returned from ProductService: " + productDto);
        return productDto;
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setCategoryId(productDto.getCategoryId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImagesUrl(productDto.getImagesUrl());
        return product;
    }

    private Product getProductByIdEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}