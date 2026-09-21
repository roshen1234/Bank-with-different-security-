package com.eazybytes.springsecsection1.controller;


import com.eazybytes.springsecsection1.doa.AccountsRepository;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.entity.Accounts;
import com.eazybytes.springsecsection1.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountsRepository accountsRepository;
    private final UserDAO userDAO;
    @GetMapping("/myAccount")
    public Accounts getAccountDetails(@RequestParam String email) {
        User user=userDAO.findUserByUsername(email);
        if(user!=null)
        {
            Accounts accounts = accountsRepository.findByCustomerId(user.getId());
            if (accounts != null) {
                return accounts;
            } else {
                return null;
            }
        }
        else{
            return null;
        }

    }

}
