package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;
import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;
import com.mohil_bansal.sellerservice.seller_service.entity.ProductOffering;
import com.mohil_bansal.sellerservice.seller_service.entity.Seller;
import com.mohil_bansal.sellerservice.seller_service.exception.DataAlreadyExistsException;
import com.mohil_bansal.sellerservice.seller_service.exception.ResourceNotFoundException;
import com.mohil_bansal.sellerservice.seller_service.repository.ProductOfferingRepository;
import com.mohil_bansal.sellerservice.seller_service.repository.SellerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;


    @Override
    public SellerDto addSeller(SellerDto sellerDto) {
        log.info("Adding new seller");
        Seller seller = new Seller();
        BeanUtils.copyProperties(sellerDto, seller);
        if (sellerRepository.findByEmail(sellerDto.getEmail()) != null) {
            log.error("Seller with Email already exists");
            throw new DataAlreadyExistsException("Seller with email " + sellerDto.getEmail() + " already exists");
        }
        sellerRepository.save(seller);
        return sellerDto;
    }

    @Override
    public SellerDto getSellerById(Long id) {
        log.info("Getting seller by id : " + id);
        Seller seller = sellerRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Seller not found with id: " + id));
        return convertToDto(seller);
    }

    @Override
    public List<SellerDto> getAllSellers() {
        log.info("Getting all sellers");
        List<Seller> sellers = sellerRepository.findAll();
        return sellers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    SellerDto convertToDto(Seller seller) {
        SellerDto sellerDto = new SellerDto();
        BeanUtils.copyProperties(seller, sellerDto);
        return sellerDto;
    }


    @Autowired
    private ProductOfferingRepository productOfferingRepository;

    @Override
    public List<ProductOfferingDto> getProductOfferingsBySeller(Long sellerId) {
        log.info("Getting product offerings for seller id : " + sellerId);
        Seller seller = sellerRepository.findById(sellerId).orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));
        List<ProductOffering> offerings = productOfferingRepository.findBySellerId(sellerId);
        if (offerings.isEmpty()) {
            log.warn("No product offerings found for seller id: " + sellerId);
            throw new ResourceNotFoundException("No product offerings found for seller id: " + sellerId);
        }
        return offerings.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductOfferingDto addProductOffering(ProductOfferingDto offeringDto) {
        log.info("Adding product offering for seller id: " + offeringDto.getSellerId());
        if (!sellerRepository.existsById(offeringDto.getSellerId())) {
            log.error("Seller not found with id: " + offeringDto.getSellerId());
            throw new ResourceNotFoundException("Seller not found with id: " + offeringDto.getSellerId());
        }
        if (productOfferingRepository.existsBySellerIdAndProductId(offeringDto.getSellerId(), offeringDto.getProductId())) {
            log.error("Product offering already exists for seller id: " + offeringDto.getSellerId() + " and product id: " + offeringDto.getProductId());
            throw new DataAlreadyExistsException("Product offering already exists for this seller and product");
        }
        ProductOffering offering = convertToEntity(offeringDto);
        offering = productOfferingRepository.save(offering);
        return convertToDto(offering);
    }

    private ProductOfferingDto convertToDto(ProductOffering offering) {
        return new ProductOfferingDto(
                offering.getId(),
                offering.getSellerId(),
                offering.getProductId(),
                offering.getPrice(),
                offering.getStock()
        );
    }

    private ProductOffering convertToEntity(ProductOfferingDto dto) {
        return new ProductOffering(
                dto.getId(),
                dto.getSellerId(),
                dto.getProductId(),
                dto.getPrice(),
                dto.getStock(),
                null
        );
    }
}
