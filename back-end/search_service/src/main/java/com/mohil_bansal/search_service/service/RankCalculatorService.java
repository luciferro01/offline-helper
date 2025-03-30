package com.mohil_bansal.search_service.service;

import com.mohil_bansal.search_service.entity.ProductOffering;
import com.mohil_bansal.search_service.repository.ProductOfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RankCalculatorService {

    @Autowired
    private ProductOfferingRepository productOfferingRepository;

    public double calculateRank(ProductOffering offering) {
        double priceWeight = 0.25;
        double stockWeight = 0.1;
        double sellerRatingWeight = 0.2;
        double reviewsWeight = 0.15;
        double productsSoldCountWeight = 0.2;
        double totalOfferingsWeight = 0.10;

        // Ranking formula (Update later) (Create a better formula)
        double rank =
                 (1 / offering.getPrice() * priceWeight)
                + (offering.getStock() * stockWeight)
                + (offering.getSellerRating() * sellerRatingWeight)
                + (offering.getProductReviews() * reviewsWeight)
                + (offering.getProductsSoldCount() * productsSoldCountWeight)
                + (offering.getTotalProductsOffered() * totalOfferingsWeight);
        return rank;
    }

    public void updateRank(ProductOffering offering) {
        //It is creating one more document again and agin rather than updating.
        //Trying to fix by us ing findById and then updating the document.

        offering.setSellerRank(calculateRank(offering));
        productOfferingRepository.save(offering);

        // Fixed for now
        //TODO: Fix recreation of document again and again
//        Optional<ProductOffering> existingOffering = productOfferingRepository.findById((offering.getId()));
//
//        if (existingOffering.isPresent()) {
//            ProductOffering offeringToUpdate = existingOffering.get();
//            offeringToUpdate.setSellerRank(calculateRank(offering));
//            productOfferingRepository.save(offeringToUpdate);
//        } else {
//            // If for some reason the document doesn't exist, just update the one we have
//            offering.setSellerRank(calculateRank(offering));
//            productOfferingRepository.save(offering);
//        }
    }

    public void recalculateAllSellerRanks() {
        Iterable<ProductOffering> allOfferings = productOfferingRepository.findAll();
        allOfferings.forEach(this::updateRank);
    }
}
