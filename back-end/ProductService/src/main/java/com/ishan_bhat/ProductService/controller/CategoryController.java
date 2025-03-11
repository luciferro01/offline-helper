package com.ishan_bhat.ProductService.controller;

import com.ishan_bhat.ProductService.dto.CategoryDto;
import com.ishan_bhat.ProductService.service.CategoryService;
import com.ishan_bhat.ProductService.utils.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<CommonResponse<List<CategoryDto>>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(CommonResponse.success(categories, HttpStatus.OK.value(), "Categories fetched successfully"));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryDto>> getCategoryById(@PathVariable Long categoryId) {
        CategoryDto category = categoryService.getCategoryById(categoryId);
        return ResponseEntity.ok(CommonResponse.success(category, HttpStatus.OK.value(), "Category fetched successfully"));
    }

    @PostMapping
    public ResponseEntity<CommonResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto createdCategory = categoryService.createCategory(categoryDto);
        return new ResponseEntity<>(CommonResponse.success(createdCategory, HttpStatus.CREATED.value(), "Category created successfully"), HttpStatus.CREATED);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<CategoryDto>> updateCategory(@PathVariable Long categoryId, @Valid @RequestBody CategoryDto categoryDtoDetails) {
        CategoryDto updatedCategory = categoryService.updateCategory(categoryId, categoryDtoDetails);
        return ResponseEntity.ok(CommonResponse.success(updatedCategory, HttpStatus.OK.value(), "Category updated successfully"));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<CommonResponse<Void>> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(CommonResponse.success(null, HttpStatus.NO_CONTENT.value(), "Category deleted successfully"), HttpStatus.NO_CONTENT);
    }
}