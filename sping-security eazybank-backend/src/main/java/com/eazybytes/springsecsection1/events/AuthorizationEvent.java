package com.eazybytes.springsecsection1.events;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.stereotype.Component;

@Component
//the below help us to use log without defining it like log=logger. but to use that we need to add a dependency lombok
@Slf4j
public class AuthorizationEvent {

    @EventListener
    public void onFailure(AuthorizationDeniedEvent deniedEvent) {
        log.error("Authorization failed for the user: {} due to: {}",
                deniedEvent.getAuthentication().get().getName(), deniedEvent.getAuthorizationResult().toString());
    }
}
