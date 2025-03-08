package com.ishan_bhat.ProductService.service;

import com.ishan_bhat.ProductService.entity.Product;
import com.ishan_bhat.ProductService.exception.ResourceNotFoundException;
import com.ishan_bhat.ProductService.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

//    public Product updateProduct(Integer id, Product productDetails) {
//        Product product = getProductById(id); // Ensure product exists
//        product.setCategoryId(productDetails.getCategoryId());
//        product.setName(productDetails.getName());
//        product.setDescription(productDetails.getDescription());
//        product.setImages(productDetails.getImages());
//        return productRepository.save(product);
//    }
    public Product updateProduct(Integer id, Product productDetails) {
        Product product = getProductById(id);

        if (productDetails.getName() != null) {
            product.setName(productDetails.getName());
        }
        if (productDetails.getDescription() != null) {
            product.setDescription(productDetails.getDescription());
        }
        if (productDetails.getImages() != null) {
            product.setImages(productDetails.getImages());
        }
        // categoryId intentionally NOT updated here in PATCH

        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = getProductById(id); // Ensure product exists
        productRepository.delete(product);
    }
}
