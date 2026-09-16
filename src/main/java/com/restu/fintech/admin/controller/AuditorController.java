package com.restu.fintech.admin.controller;

import com.restu.fintech.account.dtos.AccountDTO;
import com.restu.fintech.admin.services.AuditorService;
import com.restu.fintech.auth_users.dtos.UserDTO;
import com.restu.fintech.transaction.dtos.TransactionDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit")
public class AuditorController {

    private final AuditorService auditorService;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
    @GetMapping("/totals")
    public ResponseEntity<Map<String, Long>> getSystemTotals() {
        return ResponseEntity.ok(auditorService.getSystemsTotals());
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
    @GetMapping("/users")
    public ResponseEntity<UserDTO> findUserByEmail(@RequestParam String email) {

        Optional<UserDTO> userDTO = auditorService.findUserByEmail(email);

        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountDTO> findAccountDetailsByAccountNumber(@RequestParam String accountNumber) {

        Optional<AccountDTO> accountDTO = auditorService.findAccountDetailsByAccountNumber(accountNumber);

        return accountDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
    @GetMapping("/transactions/by-account")
    public ResponseEntity<List<TransactionDTO>> findTransactionsByAccountNumber(@RequestParam String accountNumber) {

        List<TransactionDTO> transactionDTOS = auditorService.findTransactionsByAccountNumber(accountNumber);

        if (transactionDTOS .isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(transactionDTOS);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AUDITOR')")
    @GetMapping("/transaction/by-id")
    public ResponseEntity<TransactionDTO> findTransactionById(@RequestParam Long id) {

        Optional<TransactionDTO> transactionDTO = auditorService.findTransactionById(id);

        return transactionDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
