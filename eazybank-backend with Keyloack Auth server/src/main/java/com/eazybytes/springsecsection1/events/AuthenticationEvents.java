package com.eazybytes.springsecsection1.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
//the below help us to use log without defining it like log=logger. but to use that we need to add a dependency lombok
@Slf4j
public class AuthenticationEvents {

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent success) {
        log.info("Login successful for the user: {}", success.getAuthentication().getName());
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent failure) {
        log.info("Login failed for the user: {} due to: {}",
                failure.getAuthentication().getName(), failure.getException().getMessage());
    }
}
