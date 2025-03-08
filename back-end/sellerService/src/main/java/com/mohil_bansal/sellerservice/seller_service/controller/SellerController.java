package com.mohil_bansal.sellerservice.seller_service.controller;


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
}
