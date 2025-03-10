package com.ishan_bhat.ProductService.service;

import com.ishan_bhat.ProductService.dto.ProductDto;
import com.ishan_bhat.ProductService.entity.Product;
import com.ishan_bhat.ProductService.exception.ResourceNotFoundException;
import com.ishan_bhat.ProductService.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductDto createProduct(ProductDto productDto) {
        Product product = convertToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return getProductById(savedProduct.getId());
    }

    public ProductDto getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
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
        if (productDtoDetails.getImages() != null) {
            productEntity.setImages(productDtoDetails.getImages());
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
        return new ProductDto(
                product.getId(),
                product.getCategoryId(),
                product.getName(),
                product.getDescription(),
                product.getImages()
        );
    }

    private Product convertToEntity(ProductDto productDto) {
        Product product = new Product();
        product.setId(productDto.getId());
        product.setCategoryId(productDto.getCategoryId());
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImages(productDto.getImages());
        return product;
    }

    private Product getProductByIdEntity(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
    }
}