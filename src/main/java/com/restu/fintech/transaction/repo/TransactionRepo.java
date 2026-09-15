package com.restu.fintech.transaction.repo;

import com.restu.fintech.account.entity.Account;
import com.restu.fintech.transaction.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    @Query("select t from Transaction t where t.account.accountNumber = :accountNumber" +
            " OR (t.transactionType = 'TRANSFER' and t.destinationAccount = :accountNumber)" +
            " ORDER BY t.transactionDate DESC")
    Page<Transaction> findByAccount_AccountNumber(String accountNumber, Pageable pageable);

    @Query("select t from Transaction t where t.account.accountNumber = :accountNumber" +
            " OR (t.transactionType = 'TRANSFER' and t.destinationAccount = :accountNumber)" +
            " ORDER BY t.transactionDate DESC")
    List<Transaction> findByAccount_AccountNumber(String accountNumber);
}
