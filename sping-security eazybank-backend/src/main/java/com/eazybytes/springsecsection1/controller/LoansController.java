package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.doa.LoansRepository;
import com.eazybytes.springsecsection1.entity.Loans;
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

    @GetMapping("/myLoans")
    @PreAuthorize("hasRole('USER)")
    public List<Loans> getLoanDetails(@RequestParam long id) {
        List<Loans> loans = loanRepository.findByCustomerIdOrderByStartDtDesc(id);
        if (loans != null) {
            return loans;
        } else {
            return null;
        }
    }

}
