package com.eazybytes.springsecsection1.controller;


import com.eazybytes.springsecsection1.doa.AccountTransactionsRepository;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.entity.AccountTransactions;
import com.eazybytes.springsecsection1.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final AccountTransactionsRepository accountTransactionsRepository;
    private final UserDAO userDAO;

    @GetMapping("/myBalance")
    public List<AccountTransactions> getBalanceDetails(@RequestParam String userName) {

        User user=userDAO.findUserByUsername(userName);

        if(user!=null) {
            List<AccountTransactions> accountTransactions = accountTransactionsRepository.
                    findByCustomerIdOrderByTransactionDtDesc(user.getId());
            if (accountTransactions != null) {
                return accountTransactions;
            } else {
                return null;
            }

        }
        else
            return null;
    }
}
