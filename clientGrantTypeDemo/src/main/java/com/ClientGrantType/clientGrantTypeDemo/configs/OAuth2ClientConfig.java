package com.ClientGrantType.clientGrantTypeDemo.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.client.OAuth2ClientHttpRequestInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class OAuth2ClientConfig {

    @Bean
    RestClient restClient(
            OAuth2AuthorizedClientManager authorizedClientManager) {

        //This interseptor adds in the header of the request Bearer token:"Access token" by first going into authorizedClientManager and there we return existing accesstoken or create new one if not existing
        OAuth2ClientHttpRequestInterceptor interceptor =
                new OAuth2ClientHttpRequestInterceptor(
                        authorizedClientManager);

        //This is used to mention which client registration we need to use
        interceptor.setClientRegistrationIdResolver(
                request -> "backend-b");

        return RestClient.builder()
                //this below line calls the interseptor before the request to api is send
                .requestInterceptor(interceptor)
                .build();
    }

    @Bean
    OAuth2AuthorizedClientManager authorizedClientManager(
            //this contains the client details like client secreat etc that we defined in the property file (we may have registered with different auth server but what we mentioned here "interceptor.setClientRegistrationIdResolver" that detail is only selected even though it was contain other auth server details)
            ClientRegistrationRepository clientRegistrationRepository,
            //this is used to store the access token, if access token is already there then we return this otherwise we create and store in this and return
            OAuth2AuthorizedClientService authorizedClientService) {

        //here we mention the grant type
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();

        //here we declare the AuthorizedClientManager which manages the creation or use of existing access token
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository,
                        authorizedClientService);

        //then we set the access token in authorizedClientProvider and then return back to restClient
        manager.setAuthorizedClientProvider(authorizedClientProvider);

        return manager;
    }


}
