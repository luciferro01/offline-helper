package com.ishan_bhat.ProductService.service;

import com.ishan_bhat.ProductService.dto.CategoryDto;
import com.ishan_bhat.ProductService.entity.Category;
import com.ishan_bhat.ProductService.exception.ResourceNotFoundException;
import com.ishan_bhat.ProductService.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository; // Final and no @Autowired

    public CategoryService(CategoryRepository categoryRepository) { // Constructor Injection
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDto> getAllCategories() { // Return List<CategoryDto>
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoryById(Long categoryId) { // Parameter is Long, Return type is CategoryDto
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
        return convertToDto(category);
    }

    public CategoryDto createCategory(CategoryDto categoryDto) { // Parameter and Return type are CategoryDto
        Category category = convertToEntity(categoryDto); // Convert Dto to Entity
        Category savedCategory = categoryRepository.save(category);
        return convertToDto(savedCategory); // Convert Entity to Dto
    }

    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDtoDetails) { // Parameter is Long, Parameter and Return type are CategoryDto
        Category category = getCategoryByIdEntity(categoryId); // Get Entity from helper method
        category.setName(categoryDtoDetails.getName());
        Category updatedCategory = categoryRepository.save(category);
        return convertToDto(updatedCategory);
    }

    public void deleteCategory(Long categoryId) { // Parameter is Long
        Category category = getCategoryByIdEntity(categoryId); // Get Entity from helper method
        categoryRepository.delete(category);
    }

    private CategoryDto convertToDto(Category category) {
        return new CategoryDto(
                category.getId(), // No change needed, getId() returns Long now
                category.getName()
        );
    }

    private Category convertToEntity(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());     // No change needed, setId(Long) now
        category.setName(categoryDto.getName());
        return category;
    }

    private Category getCategoryByIdEntity(Long categoryId) { // Helper method to get Category Entity
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}