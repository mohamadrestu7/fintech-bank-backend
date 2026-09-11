package com.restu.fintech.account.services;

import com.restu.fintech.account.dtos.AccountDTO;
import com.restu.fintech.account.entity.Account;
import com.restu.fintech.auth_users.entity.User;
import com.restu.fintech.enums.AccountType;
import com.restu.fintech.res.Response;

import java.util.List;

public interface AccountService {
    Account createAccount(AccountType accountType, User user);

    Response<List<AccountDTO>> getMyAccounts();

    Response<?> closeAccount(String accountNumber);
}
