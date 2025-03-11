package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;
import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;
import com.mohil_bansal.sellerservice.seller_service.entity.ProductOffering;
import com.mohil_bansal.sellerservice.seller_service.entity.Seller;
import com.mohil_bansal.sellerservice.seller_service.event.ProductOfferingUpdateEvent;
import com.mohil_bansal.sellerservice.seller_service.exception.DataAlreadyExistsException;
import com.mohil_bansal.sellerservice.seller_service.exception.ResourceNotFoundException;
import com.mohil_bansal.sellerservice.seller_service.repository.ProductOfferingRepository;
import com.mohil_bansal.sellerservice.seller_service.repository.SellerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private KafkaTemplate<String, ProductOfferingUpdateEvent> kafkaTemplate;

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

    @Override
    public SellerDto updateSeller(Long sellerId, SellerDto sellerDto) {
        log.info("Updating seller with id: " + sellerId);
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));

        if (sellerDto.getTotalStock() != null) {
            seller.setTotalStock(sellerDto.getTotalStock());
        }
        if (sellerDto.getTotalSold() != null) {
            seller.setTotalSold(sellerDto.getTotalSold());
        }
        if (sellerDto.getRating() != null) {
            seller.setRating(sellerDto.getRating());
        }

        seller = sellerRepository.save(seller);
        return convertToDto(seller);
    }

    SellerDto convertToDto(Seller seller) {
        SellerDto sellerDto = new SellerDto();
        BeanUtils.copyProperties(seller, sellerDto);
        return sellerDto;
    }


    @Autowired
    private ProductOfferingRepository productOfferingRepository;

    @Override
    public ProductOfferingDto getProductOffering(Long productOfferingId) {
        log.info("Getting product offering : " + productOfferingId);
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId).orElseThrow(() -> new ResourceNotFoundException("Product Offering not found with id: " + productOfferingId));
        return convertToDto(productOffering);
    }
    
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



        // Publish Kafka Event for Product Offering Creation
//        ProductOfferingDto savedOfferingDto = convertToDto(offering);
        ProductOfferingUpdateEvent createEvent = new ProductOfferingUpdateEvent("CREATE", offeringDto);
        kafkaTemplate.send("product-updates", String.valueOf(offeringDto.getId()), createEvent);



        Seller seller = sellerRepository.findById(offeringDto.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + offeringDto.getSellerId()));
        seller.setTotalStock(seller.getTotalStock() + offering.getStock());
        seller.setTotalSold(seller.getTotalSold() + offering.getSold());
        sellerRepository.save(seller);

        return convertToDto(offering);
    }

    @Override
    public ProductOfferingDto updateProductOffering(Long productOfferingId, ProductOfferingDto offeringDto) {
        log.info("Updating product offering with id: " + productOfferingId);
        ProductOffering offering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException("Product offering not found with id: " + productOfferingId));

        int oldStock = offering.getStock();
        int oldSold = offering.getSold();

        if (offeringDto.getPrice() != null) {
            offering.setPrice(offeringDto.getPrice());
        }
        if (offeringDto.getStock() != null) {
            offering.setStock(offeringDto.getStock());
        }
        if (offeringDto.getSold() != null) {
            offering.setSold(offeringDto.getSold());
        }
        if (offeringDto.getRating() != null) {
            offering.setRating(offeringDto.getRating());
        }

        offering = productOfferingRepository.save(offering);



        // Publish Kafka Event for Product Offering Update
        ProductOfferingDto updatedOfferingDto = convertToDto(offering);
        ProductOfferingUpdateEvent updateEvent = new ProductOfferingUpdateEvent("UPDATE", updatedOfferingDto);
        kafkaTemplate.send("product-updates", String.valueOf(updatedOfferingDto.getId()), updateEvent);



        Seller seller = sellerRepository.findById(offering.getSellerId())
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + offeringDto.getSellerId()));
        if (seller.getTotalSold() > 0 && offering.getSold() > 0) {
            int totalUnitsSold = seller.getTotalSold() + offering.getSold();
            double weightedRating = ((seller.getRating() * seller.getTotalSold()) + (offering.getRating() * offering.getSold())) / totalUnitsSold;
            seller.setRating(weightedRating);
        }
        seller.setTotalStock(seller.getTotalStock() - oldStock + offering.getStock());
        seller.setTotalSold(seller.getTotalSold() - oldSold + offering.getSold());
        sellerRepository.save(seller);

        return convertToDto(offering);
    }

    private ProductOfferingDto convertToDto(ProductOffering offering) {
        return new ProductOfferingDto(
                offering.getId(),
                offering.getSellerId(),
                offering.getProductId(),
                offering.getSellerName(),
                offering.getProductName(),
                offering.getPrice(),
                offering.getStock(),
                offering.getSold(),
                offering.getRating()
        );
    }

    private ProductOffering convertToEntity(ProductOfferingDto dto) {
        return new ProductOffering(
                dto.getId(),
                dto.getSellerId(),
                dto.getProductId(),
                dto.getSellerName(),
                dto.getProductName(),
                dto.getPrice(),
                dto.getStock(),
                dto.getSold(),
                dto.getRating(),
                null
        );
    }
}