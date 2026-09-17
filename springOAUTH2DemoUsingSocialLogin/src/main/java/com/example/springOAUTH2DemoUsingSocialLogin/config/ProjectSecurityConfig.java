package com.example.springOAUTH2DemoUsingSocialLogin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ProjectSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    {
        httpSecurity.authorizeHttpRequests(request->request.requestMatchers("/secure").authenticated()
                .anyRequest().permitAll()
        );

        httpSecurity.formLogin(Customizer.withDefaults());
        httpSecurity.oauth2Login(Customizer.withDefaults());

        return httpSecurity.build();
    }

    //the below given step is one way to use SSO login using OAUTH 2 second method is adding in properties so no need of all these code
//    @Bean
//    ClientRegistrationRepository clientRegistrationRepository(){
//        org.springframework.security.oauth2.client.registration.ClientRegistration github = githubClientRegistration();
//        ClientRegistration facebook=facebookClientRegistration();
//
//        return new InMemoryClientRegistrationRepository(github,facebook);
//    }
//
//    private ClientRegistration githubClientRegistration()
//    {
//        return CommonOAuth2Provider.GITHUB.getBuilder("github").clientId("Ov23lixMy6bfslEqKw8D")
//                .clientSecret("831747848cd15ca9518096e322efa01d8308fca2").build();
//    }
//
//    private ClientRegistration facebookClientRegistration()
//    {
//        return CommonOAuth2Provider.FACEBOOK.getBuilder("facebook").clientId("1989988658346097")
//                .clientSecret("1dc11b198c88b841313346608a65d927").build();
//    }

}
