package com.restu.fintech.account.dtos;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.restu.fintech.auth_users.dtos.UserDTO;
import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.enums.AccountStatus;
import com.restu.fintech.enums.AccountType;
import com.restu.fintech.enums.Currency;
import com.restu.fintech.transaction.dtos.TransactionDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {

    private Long id;

    private String accountNumber;

    private BigDecimal balance;

    private AccountType accountType;

    @JsonBackReference //this will not be added to the account dto. it will be ignored because it is a back reference
    private UserDTO user;

    private Currency currency;

    private AccountStatus status;

    @JsonManagedReference //it helps avoid recursion loop by ignoring the AccountDTO within the TransactionDTO
    private List<TransactionDTO> transactions;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
