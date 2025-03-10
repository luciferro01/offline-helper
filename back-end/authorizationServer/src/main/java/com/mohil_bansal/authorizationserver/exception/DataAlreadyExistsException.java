package com.mohil_bansal.authorizationserver.exception;

public class DataAlreadyExistsException extends RuntimeException{

    public  DataAlreadyExistsException(String message) {
        super(message);
    }
}