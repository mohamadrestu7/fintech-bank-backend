package com.restu.fintech.exceptions;

public class InvalidTransactionException extends RuntimeException{
    public InvalidTransactionException(String error) {
        super(error);
    }
}
