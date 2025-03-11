package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.client.ProductServiceClient;
import com.mohil_bansal.sellerservice.seller_service.dto.ProductDto;
import com.mohil_bansal.sellerservice.seller_service.dto.ProductOfferingDto;
import com.mohil_bansal.sellerservice.seller_service.dto.SearchProductOfferingDto;
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
    private ProductOfferingRepository productOfferingRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

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
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + id));
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

    @Override
    public ProductOfferingDto getProductOffering(Long productOfferingId) {
        log.info("Getting product offering : " + productOfferingId);
        ProductOffering productOffering = productOfferingRepository.findById(productOfferingId).orElseThrow(
                () -> new ResourceNotFoundException("Product Offering not found with id: " + productOfferingId));
        return convertToDto(productOffering);
    }

    @Override
    public List<ProductOfferingDto> getProductOfferingsBySeller(Long sellerId) {
        log.info("Getting product offerings for seller id : " + sellerId);
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with id: " + sellerId));
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
    public ProductOfferingDto updateProductOffering(Long productOfferingId, ProductOfferingDto offeringDto) {
        log.info("Updating product offering with id: " + productOfferingId);
        ProductOffering offering = productOfferingRepository.findById(productOfferingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product offering not found with id: " + productOfferingId));

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


        Seller seller = sellerRepository.findById(offering.getSellerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Seller not found with id: " + offeringDto.getSellerId()));
        if (seller.getTotalSold() > 0 && offering.getSold() > 0) {
            int totalUnitsSold = seller.getTotalSold() + offering.getSold();
            double weightedRating = ((seller.getRating() * seller.getTotalSold())
                    + (offering.getRating() * offering.getSold())) / totalUnitsSold;
            seller.setRating(weightedRating);
        }
        seller.setTotalStock(seller.getTotalStock() - oldStock + offering.getStock());
        seller.setTotalSold(seller.getTotalSold() - oldSold + offering.getSold());
        sellerRepository.save(seller);


        // Search Product Offering Update

        ProductDto productDetails = productServiceClient
                .getProductById(offeringDto.getProductId()).getData();

        int totalProductsOffered = getProductOfferingsBySeller(seller.getId()).size();

        SearchProductOfferingDto searchProductOfferingDto = new SearchProductOfferingDto();

        searchProductOfferingDto.setProductOfferingId(String.valueOf(offering.getId()));
        searchProductOfferingDto.setProductId(productDetails.getId());
        searchProductOfferingDto.setProductName(productDetails.getName());
        searchProductOfferingDto.setSellerId(offering.getSellerId());
        searchProductOfferingDto.setSellerName(offering.getSellerName());
        searchProductOfferingDto.setPrice(offering.getPrice());
        searchProductOfferingDto.setStock(offering.getStock());
        searchProductOfferingDto.setSellerRating(seller.getRating());
        searchProductOfferingDto.setTotalProductsOffered(totalProductsOffered);
        searchProductOfferingDto.setProductsSoldCount(seller.getTotalSold());
        searchProductOfferingDto.setProductReviews(0);
        searchProductOfferingDto.setCategory("Electronics");
        String id = offering.getId() + "-" + offering.getProductId();
        searchProductOfferingDto.setId(id);


        // Publish Kafka Event for Product Offering Update
        ProductOfferingUpdateEvent updateEvent = new ProductOfferingUpdateEvent("UPDATE", searchProductOfferingDto);
        kafkaTemplate.send("product-updates", String.valueOf(searchProductOfferingDto.getId()), updateEvent);

        ProductOfferingDto updatedOfferingDto = convertToDto(offering);
        return convertToDto(offering);
    }

    @Override
    public ProductOfferingDto addProductOffering(ProductOfferingDto offeringDto) {
        log.info("Adding product offering for seller id: " + offeringDto.getSellerId());
        Seller seller = sellerRepository.findById(offeringDto.getSellerId()) // Fetch Seller Entity
                .orElseThrow(
                        () -> new ResourceNotFoundException("Seller not found with id: " + offeringDto.getSellerId()));

        String productName = null;
        String productImageUrl = null;

        try {
            ProductDto productDetails = productServiceClient
                    .getProductById(offeringDto.getProductId()).getData();
            System.out.println("ProductDto received from ProductServiceClient: " + productDetails);

            log.info("Product ID {} validated successfully with Product Service.", offeringDto.getProductId());

            if (productDetails != null) {
                productName = productDetails.getName();
                productImageUrl = productDetails.getImagesUrl();
                log.info("Product Name: {}, Images: {} fetched from Product Service", productName, productImageUrl);
            }

        } catch (Exception e) {
            log.error("Product ID {} not found in Product Service. Product Offering creation failed.",
                    offeringDto.getProductId());
            throw new ResourceNotFoundException(
                    "Product not found with id: " + offeringDto.getProductId() + " in Product Catalog.");
        }

        if (productOfferingRepository.existsBySellerIdAndProductId(offeringDto.getSellerId(),
                offeringDto.getProductId())) {
            log.error("Product offering already exists for seller id: " + offeringDto.getSellerId()
                    + " and product id: " + offeringDto.getProductId());
            throw new DataAlreadyExistsException("Product offering already exists for this seller and product");
        }

        String sellerName = seller.getName();// Get sellerName from Seller entity
        log.info("Values before convertToEntity: offeringDto={}, productName={}, productImageUrl={}, sellerName={}",
                offeringDto, productName, productImageUrl, sellerName); // LOGGING
        ProductOffering offering = convertToEntity(offeringDto, sellerName, productName, productImageUrl); // Pass



        offering = productOfferingRepository.save(offering);
        seller.setTotalStock(seller.getTotalStock() + offering.getStock());
        seller.setTotalSold(seller.getTotalSold() + offering.getSold());
        sellerRepository.save(seller);
                                                                                                           // sellerName
        // Search Product Offering Kafka
        ProductDto productDetails = productServiceClient
                .getProductById(offeringDto.getProductId()).getData();

        int totalProductsOffered = getProductOfferingsBySeller(seller.getId()).size();

        SearchProductOfferingDto searchProductOfferingDto = new SearchProductOfferingDto();

        searchProductOfferingDto.setProductOfferingId(String.valueOf(offering.getId()));
        searchProductOfferingDto.setProductId(productDetails.getId());
        searchProductOfferingDto.setProductName(productDetails.getName());
        searchProductOfferingDto.setSellerId(offering.getSellerId());
        searchProductOfferingDto.setSellerName(offering.getSellerName());
        searchProductOfferingDto.setPrice(offering.getPrice());
        searchProductOfferingDto.setStock(offering.getStock());
        searchProductOfferingDto.setSellerRating(seller.getRating());
        searchProductOfferingDto.setTotalProductsOffered(totalProductsOffered);
        searchProductOfferingDto.setProductsSoldCount(seller.getTotalSold());
        searchProductOfferingDto.setProductReviews(0);
        searchProductOfferingDto.setCategory("Electronics");
        String id = offering.getId() + "-" + offering.getProductId();
        searchProductOfferingDto.setId(id);

        ProductOfferingUpdateEvent createEvent = new ProductOfferingUpdateEvent("CREATE", searchProductOfferingDto);
        kafkaTemplate.send("product-updates", String.valueOf(searchProductOfferingDto.getId()), createEvent);



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
                offering.getRating(),
                // offering.getImages()
                offering.getProductImageUrl());
    }

    private ProductOffering convertToEntity(ProductOfferingDto dto, String sellerName, String productName,
            String productImageUrl) {
        return new ProductOffering(
                dto.getId(),
                dto.getSellerId(),
                dto.getProductId(),
                sellerName,
                productName,
                dto.getPrice(),
                dto.getStock(),
                dto.getSold(),
                dto.getRating(),
                null, // createdAt - will be set by @PrePersist
                // productImages
                productImageUrl);
    }

}