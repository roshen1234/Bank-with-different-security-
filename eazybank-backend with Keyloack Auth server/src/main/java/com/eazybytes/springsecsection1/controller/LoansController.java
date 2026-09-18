package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.doa.LoansRepository;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.entity.Loans;
import com.eazybytes.springsecsection1.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoansController {

    private final LoansRepository loanRepository;
    private final UserDAO userDAO;

    @GetMapping("/myLoans")
    @PreAuthorize("hasRole('USER')")
    public List<Loans> getLoanDetails(@RequestParam  String userName) {
        User user=userDAO.findUserByUsername(userName);
        if(user!=null) {
            List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(user.getId());
            if (loans != null) {
                return loans;
            } else {
                return null;
            }
        }
        else
            return null;
    }

}
