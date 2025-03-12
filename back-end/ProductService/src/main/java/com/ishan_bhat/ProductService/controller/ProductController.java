package com.ishan_bhat.ProductService.controller;

import com.ishan_bhat.ProductService.dto.CategoryDto;
import com.ishan_bhat.ProductService.dto.ProductDto;
import com.ishan_bhat.ProductService.service.ProductService;
import com.ishan_bhat.ProductService.utils.CommonResponse;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<ProductDto>>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(CommonResponse.success(products, HttpStatus.OK.value(), "Products fetched successfully"));
    }

//    @GetMapping("/{productId}")
//    public ResponseEntity<CommonResponse<ProductDto>> getProductById(@PathVariable Long productId) {
//        System.out.println("Hello");
//        ProductDto product = productService.getProductById(productId);
//        return ResponseEntity.ok(CommonResponse.success(product, HttpStatus.OK.value(), "Product fetched successfully"));
//    }
    @GetMapping("/{productId}")
    @ResponseBody
    public CommonResponse<ProductDto> getProductById(@PathVariable Long productId) {
        log.info("Fetching product with ID: {}", productId);
        ProductDto product = productService.getProductById(productId);
        return CommonResponse.success(product, HttpStatus.OK.value(), "Product fetched successfully");
    }

    @PostMapping
    public ResponseEntity<CommonResponse<ProductDto>> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto createdProduct = productService.createProduct(productDto);
        return new ResponseEntity<>(CommonResponse.success(createdProduct, HttpStatus.CREATED.value(), "Product created successfully"), HttpStatus.CREATED);
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<CommonResponse<ProductDto>> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductDto productDtoDetails) {
        ProductDto updatedProduct = productService.updateProduct(productId, productDtoDetails);
        return ResponseEntity.ok(CommonResponse.success(updatedProduct, HttpStatus.OK.value(), "Product updated successfully"));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<CommonResponse<Void>> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build(); // No content for delete, response is CommonResponse<Void> or HttpStatus.NO_CONTENT
    }

//    @GetMapping("/getCategory/{id}")
//    public ResponseEntity<CommonResponse<CategoryDto>>
}