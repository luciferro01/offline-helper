package com.mohil_bansal.sellerservice.seller_service.service;

import com.mohil_bansal.sellerservice.seller_service.dto.SellerDto;
import com.mohil_bansal.sellerservice.seller_service.entity.Seller;
import com.mohil_bansal.sellerservice.seller_service.exception.DataAlreadyExistsException;
import com.mohil_bansal.sellerservice.seller_service.exception.ResourceNotFoundException;
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

    @Override
    public void getAllSellerProductOfferings() {

    }

    SellerDto convertToDto(Seller seller) {
        SellerDto sellerDto = new SellerDto();
        BeanUtils.copyProperties(seller, sellerDto);
        return sellerDto;
    }
}
