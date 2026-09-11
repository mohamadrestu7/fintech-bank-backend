package com.restu.fintech.admin.services;

import com.restu.fintech.account.dtos.AccountDTO;
import com.restu.fintech.auth_users.dtos.UserDTO;
import com.restu.fintech.transaction.dtos.TransactionDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditorService {

    Map<String, Long> getSystemsTotals();
    Optional<UserDTO> findUserByEmail(String email);
    Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber);
    List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber);
    Optional<TransactionDTO> findTransactionById(Long transactionId);
}
