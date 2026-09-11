package com.restu.fintech.exceptions;

public class InsufficientBalanceException extends RuntimeException{
    public InsufficientBalanceException(String error) {
        super(error);
    }
}
