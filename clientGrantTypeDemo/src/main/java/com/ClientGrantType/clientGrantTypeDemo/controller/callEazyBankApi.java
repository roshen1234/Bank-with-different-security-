package com.ClientGrantType.clientGrantTypeDemo.controller;

import com.ClientGrantType.clientGrantTypeDemo.dto.Account;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class callEazyBankApi {

    RestClient restClient;

    public callEazyBankApi(RestClient restClient)
    {
        this.restClient=restClient;
    }

    @GetMapping("/myAccount")
    public Account getAccountDetails() {

        //this is to call other backend apis (this is generic way not specific to Oauth)
        return restClient
                .get()
                .uri("http://localhost:8080/myAccount?email=happy@example.com")
                .retrieve()
                .body(Account.class);
    }



}
