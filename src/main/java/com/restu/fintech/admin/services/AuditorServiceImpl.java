package com.restu.fintech.admin.services;

import com.restu.fintech.account.dtos.AccountDTO;
import com.restu.fintech.account.repo.AccountRepo;
import com.restu.fintech.auth_users.dtos.UserDTO;
import com.restu.fintech.auth_users.repo.UserRepo;
import com.restu.fintech.transaction.dtos.TransactionDTO;
import com.restu.fintech.transaction.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditorServiceImpl implements AuditorService {

    private final UserRepo userRepo;
    private final AccountRepo accountRepo;
    private final TransactionRepo transactionRepo;
    private final ModelMapper modelMapper;

    @Override
    public Map<String, Long> getSystemsTotals() {

        long totalUsers = userRepo.count();
        long totalAccount = accountRepo.count();
        long totalTransaction = transactionRepo.count();

        return Map.of(
                "totalUsers", totalUsers,
                "totalAccounts", totalAccount,
                "totalTransactions", totalTransaction
        );
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNumber(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
                .map(account -> modelMapper.map(account, AccountDTO.class));
    }

    @Override
    public List<TransactionDTO> findTransactionsByAccountNumber(String accountNumber) {
        return transactionRepo.findByAccount_AccountNumber(accountNumber)
                .stream().map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TransactionDTO> findTransactionById(Long transactionId) {
        log.info("trx id = {}",transactionId);
        return transactionRepo.findById(transactionId)
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }
}
