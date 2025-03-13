package com.mohil_bansal.search_service.controller;

import com.mohil_bansal.search_service.dto.ProductOfferingDto;
import com.mohil_bansal.search_service.entity.ProductOffering;
import com.mohil_bansal.search_service.service.SearchService;
import com.mohil_bansal.search_service.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping()
    public ResponseEntity<CommonResponse<List<ProductOffering>>> search(@RequestParam String query) {
        try {
            List<ProductOffering> results = searchService.search(query);
            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error during search", 500));
        }
    }

    @GetMapping("/productName")
    public ResponseEntity<CommonResponse<PageImpl<ProductOffering>>> searchByProductName(@RequestParam String productName, @RequestParam int page, @RequestParam int size) {
        try {
            PageImpl<ProductOffering> results = searchService.searchByProductName(productName, page, size);
            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results by product name"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error during search by product name", 500));
        }
    }

    @GetMapping("/category")
    public ResponseEntity<CommonResponse<PageImpl<ProductOffering>>> searchByCategory(@RequestParam String category, @RequestParam int page, @RequestParam int size) {
        try {
            PageImpl<ProductOffering> results = searchService.searchByCategory(category, page, size);
            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results by category"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error during search by category", 500));
        }
    }

    @GetMapping("/sellerName")
    public ResponseEntity<CommonResponse<PageImpl<ProductOffering>>> searchBySellerName(@RequestParam String sellerName, @RequestParam int page, @RequestParam int size) {
        try {
            PageImpl<ProductOffering> results = searchService.searchBySellerName(sellerName, page, size);
            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results by seller name"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error during search by seller name", 500));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<CommonResponse<PageImpl<ProductOffering>>> searchByAll(@RequestParam String query, @RequestParam int page, @RequestParam int size) {
        try {
            PageImpl<ProductOffering> results = searchService.searchByAll(query, page, size);
            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results by all"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error during search by all", 500));
        }
    }


//    @GetMapping("/productNameContainingPageable")
//    public ResponseEntity<CommonResponse<Page<ProductOffering>>> searchByProductNameContaining(@RequestParam String keyword, @RequestParam int page, @RequestParam int size) {
//        try {
//            PageImpl<ProductOffering> results = searchService.findByProductNameContaining(keyword, page, size);
//            return ResponseEntity.ok(CommonResponse.success(results, 200, "Search results by product name containing"));
//        } catch (Exception e) {
//            return ResponseEntity.ok(CommonResponse.failure("Error during search by product name containing", 500));
//        }
//    }


    @PostMapping
    public ResponseEntity<CommonResponse<ProductOfferingDto>> create(@RequestBody ProductOfferingDto productOfferingDto) {
        try {
            ProductOfferingDto createdProductOffering = searchService.create(productOfferingDto);
            return ResponseEntity.ok(CommonResponse.success(createdProductOffering, 201, "Product offering created"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error creating product offering", 500));
        }
    }

    @DeleteMapping
    public ResponseEntity<CommonResponse<String>> deleteAll() {
        try {
            searchService.deleteAll();
            return ResponseEntity.ok(CommonResponse.success("Deleted all records", 200, "Deleted All Records"));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.failure("Error deleting records", 500));
        }
    }
}