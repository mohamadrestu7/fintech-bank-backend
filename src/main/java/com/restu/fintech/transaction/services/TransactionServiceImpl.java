package com.restu.fintech.transaction.services;

import com.restu.fintech.account.entity.Account;
import com.restu.fintech.account.repo.AccountRepo;
import com.restu.fintech.account.services.AccountService;
import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.auth_users.services.UserService;
import com.restu.fintech.enums.TransactionStatus;
import com.restu.fintech.enums.TransactionType;
import com.restu.fintech.exceptions.BadRequestException;
import com.restu.fintech.exceptions.InsufficientBalanceException;
import com.restu.fintech.exceptions.InvalidTransactionException;
import com.restu.fintech.exceptions.NotFoundException;
import com.restu.fintech.notification.dtos.NotificationDTO;
import com.restu.fintech.notification.services.NotificationService;
import com.restu.fintech.res.Response;
import com.restu.fintech.transaction.dtos.TransactionDTO;
import com.restu.fintech.transaction.dtos.TransactionRequest;
import com.restu.fintech.transaction.entity.Transaction;
import com.restu.fintech.transaction.repo.TransactionRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction =  new Transaction();

        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setDescription(transaction.getDescription());

        switch (transactionRequest.getTransactionType()) {
            case DEPOSIT -> handleDeposit(transactionRequest, transaction);
            case WITHDRAWAL -> handleWithdrawal(transactionRequest, transaction);
            case TRANSFER -> handleTransfer(transactionRequest, transaction);
            default -> throw new InvalidTransactionException("Invalid transaction type");
        }

        transaction.setStatus(TransactionStatus.SUCCESS);
        Transaction savedTrx = transactionRepo.save(transaction);

        sendTransactionNotification(savedTrx);

        return Response.builder()
                .statusCode(200)
                .message("Transaction successful")
                .build();
    }

    private void sendTransactionNotification(Transaction trx) {

        User user = trx.getAccount().getUser();
        String subject;
        String template;

        Map<String, Object> templateVariables = new HashMap<>();

        templateVariables.put("name", user.getFirstName());
        templateVariables.put("amount", trx.getAmount());
        templateVariables.put("accountNumber", trx.getAccount().getAccountNumber());
        templateVariables.put("date", trx.getTransactionDate());
        templateVariables.put("balance", trx.getAccount().getBalance());

        if (trx.getTransactionType() == TransactionType.DEPOSIT) {

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);

        } else if (trx.getTransactionType() == TransactionType.WITHDRAWAL) {

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject("Debit Alert")
                    .templateName("debit-alert")
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);

        } else if (trx.getTransactionType() == TransactionType.TRANSFER) {

            NotificationDTO notificationDTO = NotificationDTO.builder()
                    .recipient(user.getEmail())
                    .subject("Debit Alert")
                    .templateName("debit-alert")
                    .templateVariables(templateVariables)
                    .build();

            notificationService.sendEmail(notificationDTO, user);


            Account destination = accountRepo.findByAccountNumber(trx.getDestinationAccount())
                    .orElseThrow(() -> new NotFoundException("Destination account not found"));

            User receiver = destination.getUser();

            Map<String, Object> receiverVars = new HashMap<>();
            receiverVars.put("name", receiver.getFirstName());
            receiverVars.put("amount", trx.getAmount());
            receiverVars.put("accountNumber", destination.getAccountNumber());
            receiverVars.put("date", trx.getTransactionDate());
            receiverVars.put("balance", destination.getBalance());


            NotificationDTO receiverNotificationDTO = NotificationDTO.builder()
                    .recipient(receiver.getEmail())
                    .subject("Credit Alert")
                    .templateName("credit-alert")
                    .templateVariables(receiverVars)
                    .build();

            notificationService.sendEmail(receiverNotificationDTO, receiver);

        }

    }

    private void handleTransfer(TransactionRequest transactionRequest, Transaction transaction) {

        Account sourceAccount = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        Account destinationAccount = accountRepo.findByAccountNumber(transactionRequest.getDestinationAccountNumber())
                .orElseThrow(() -> new NotFoundException("Destination Account not found"));

        if (sourceAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance");
        }


        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionRequest.getAmount()));
        accountRepo.save(sourceAccount);

        destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequest.getAmount()));
        accountRepo.save(destinationAccount);

        transaction.setAccount(sourceAccount);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setDestinationAccount(destinationAccount.getAccountNumber());
    }

    private void handleWithdrawal(TransactionRequest transactionRequest, Transaction transaction) {

        Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0){
            throw new InsufficientBalanceException("Insufficient balance in source account");
        }

        account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);

    }

    private void handleDeposit(TransactionRequest transactionRequest, Transaction transaction) {

        Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account not found"));

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);

    }

    @Override
    @Transactional
    public Response<List<TransactionDTO>> getTransactionsByAccountNumber(String accountNumber, int page, int size) {

        User user = userService.getCurrentLoggedInUser();

        Account account = accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Account does not belong to the authenticated user");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> trxs = transactionRepo.findByAccount_AccountNumber(accountNumber, pageable);

        List<TransactionDTO> transactionDTOS = trxs.getContent().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class))
                .toList();

        return Response.<List<TransactionDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Transactions Retrieved")
                .data(transactionDTOS)
                .meta(Map.of(
                        "currentPage", trxs.getNumber(),
                        "totalItems", trxs.getTotalElements(),
                        "totalPages", trxs.getTotalPages(),
                        "pageSize", trxs.getSize()
                ))
                .build();
    }
}
