package com.mohil_bansal.sellerservice.seller_service.controller;


import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;
import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;
import com.mohil_bansal.sellerservice.seller_service.service.SellerService;
import com.mohil_bansal.sellerservice.seller_service.utils.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

//     Add a new seller
     @PostMapping(value = "/add")
     public ResponseEntity<CommonResponse<SellerDto>> addSeller(@RequestBody SellerDto sellerDto) {
            SellerDto seller = sellerService.addSeller(sellerDto);
            CommonResponse<SellerDto> response = CommonResponse.success(seller, 200, "Seller added successfully");
            return ResponseEntity.ok(response);
     }

//     Get seller by id
        @GetMapping(value = "/get")
        public ResponseEntity<CommonResponse<SellerDto>> getSellerById(@RequestParam Long id) {
                SellerDto seller = sellerService.getSellerById(id);
                CommonResponse<SellerDto> response = CommonResponse.success(seller, 200, "Seller fetched successfully");
                return ResponseEntity.ok(response);
        }

//     Get all sellers
        @GetMapping(value = "/getAll")
        public ResponseEntity<CommonResponse<List<SellerDto>>> getAllSellers() {
                List<SellerDto> sellers = sellerService.getAllSellers();
                CommonResponse<List<SellerDto>> response = CommonResponse.success(sellers, 200, "Sellers fetched successfully");
                return ResponseEntity.ok(response);
        }

    // Update a Seller
    @PutMapping("/{sellerId}")
    public ResponseEntity<CommonResponse<SellerDto>> updateSeller(
            @PathVariable Long sellerId,
            @RequestBody SellerDto sellerDto) {
        SellerDto seller = sellerService.updateSeller(sellerId, sellerDto);
        CommonResponse<SellerDto> response = CommonResponse.success(seller, 200, "Seller updated successfully");
        return ResponseEntity.ok(response);
    }

    // Get product offerings by id
    @GetMapping("/{productOfferingId}")
    public ResponseEntity<CommonResponse<ProductOfferingDto>> getProductOffering(@PathVariable Long productOfferingId) {
        ProductOfferingDto offering = sellerService.getProductOffering(productOfferingId);
        CommonResponse<ProductOfferingDto> response = CommonResponse.success(offering, 200, "Product offering fetched successfully");
        return ResponseEntity.ok(response);
    }
    
    // Get product offerings by seller
    @GetMapping("/{sellerId}/offerings")
    public ResponseEntity<CommonResponse<List<ProductOfferingDto>>> getProductOfferings(@PathVariable Long sellerId) {
        List<ProductOfferingDto> offerings = sellerService.getProductOfferingsBySeller(sellerId);
        CommonResponse<List<ProductOfferingDto>> response = CommonResponse.success(offerings, 200, "Product offerings fetched successfully");
        return ResponseEntity.ok(response);
    }

    // Add a product offering for a seller
    @PostMapping("/{sellerId}/offerings")
    public ResponseEntity<CommonResponse<ProductOfferingDto>> addProductOffering(@PathVariable Long sellerId, @RequestBody ProductOfferingDto offeringDto) {
        offeringDto.setSellerId(sellerId);
        ProductOfferingDto addedOffering = sellerService.addProductOffering(offeringDto);
        CommonResponse<ProductOfferingDto> response = CommonResponse.success(addedOffering, 200, "Product offering added successfully");
        return ResponseEntity.ok(response);
    }

    // Update a Product Offering
    @PutMapping("/{productOfferingId}")
    public ResponseEntity<CommonResponse<ProductOfferingDto>> updateProductOffering(
            @PathVariable Long productOfferingId,
            @RequestBody ProductOfferingDto updatedOffering) {
        ProductOfferingDto offering = sellerService.updateProductOffering(productOfferingId, updatedOffering);
        CommonResponse<ProductOfferingDto> response = CommonResponse.success(offering, 200, "Product offering updated successfully");
        return ResponseEntity.ok(response);
    }
}
