package com.example.Order.exception;

public class DataAlreadyExistsException extends RuntimeException{

    public  DataAlreadyExistsException(String message) {
        super(message);
    }
}