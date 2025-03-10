package com.example.Cart.exception;

public class DataAlreadyExistsException extends RuntimeException{

    public  DataAlreadyExistsException(String message) {
        super(message);
    }
}