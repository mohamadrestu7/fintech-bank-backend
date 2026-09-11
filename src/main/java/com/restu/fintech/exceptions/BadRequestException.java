package com.restu.fintech.exceptions;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String error) {
        super(error);
    }
}
