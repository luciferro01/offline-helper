package com.mohil_bansal.sellerservice.seller_service.exception;

public class DataAlreadyExistsException extends RuntimeException{

    public  DataAlreadyExistsException(String message) {
        super(message);
    }
}