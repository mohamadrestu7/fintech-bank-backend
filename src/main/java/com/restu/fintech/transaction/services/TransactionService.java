package com.restu.fintech.transaction.services;

import com.restu.fintech.res.Response;
import com.restu.fintech.transaction.dtos.TransactionDTO;
import com.restu.fintech.transaction.dtos.TransactionRequest;

import java.util.List;

public interface TransactionService {
    Response<?> createTransaction(TransactionRequest transactionRequest);

    Response<List<TransactionDTO>> getTransactionsByAccountNumber(String AccountNumber, int page, int size);

}

