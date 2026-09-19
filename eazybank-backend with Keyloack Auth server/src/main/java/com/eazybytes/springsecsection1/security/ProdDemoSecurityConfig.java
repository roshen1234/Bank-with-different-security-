package com.eazybytes.springsecsection1.security;

import com.eazybytes.springsecsection1.exception.CustomAccessDeniedHandler;
import com.eazybytes.springsecsection1.filter.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@Profile("prod")
public class ProdDemoSecurityConfig {

    public ProdDemoSecurityConfig() {
        System.out.println(">>> ProdDemoSecurityConfig CONSTRUCTOR CALLED <<<");
    }

//    @Bean
//    public InMemoryUserDetailsManager userDetailsManager() {
//
//        UserDetails john = User.builder()
//                .username("John")
//                .password("{noop}Test@123")
//                .roles("EMPLOYEE")
//                .build();
//        UserDetails linda = User.builder()
//                .username("linda")
//                .password("{noop}Test@123")
//                .roles("EMPLOYEE","WORKER")
//                .build();
//
//        UserDetails susan = User.builder()
//                .username("susan")
//                .password("{noop}Test@123")
//                .roles("EMPLOYEE","WORKER","ADMIN")
//                .build();
//
//        return new InMemoryUserDetailsManager(john,linda,susan);
//    }

//    @Bean
//    public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource)
//    {
//        return new JdbcUserDetailsManager(dataSource);
//    }

//    @Bean
//    public JdbcUserDetailsManager JdbcUserDetailsManager(DataSource dataSource)
//    {
//        JdbcUserDetailsManager jdbcUserDetailsManager =
//                new JdbcUserDetailsManager(dataSource);
//
//        jdbcUserDetailsManager.setUsersByUsernameQuery("select userId,password,pw from members where userId=?");
//        jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("select userId,roleId from powers where userId=?");
//        return new JdbcUserDetailsManager(dataSource);
//    }

//    @Bean
//    public BCryptPasswordEncoder passwordEncoder()
//    {
//        return new BCryptPasswordEncoder();
//    }


    //this is not needed as daoAuthenticationProvider is given by default so even if we dont mention it like below it will still run by default code
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider(UserService userService,PasswordEncoder passwordEncoder,CompromisedPasswordChecker compromisedPasswordChecker) {
//        DaoAuthenticationProvider auth = new DaoAuthenticationProvider(userService);
//        auth.setPasswordEncoder(passwordEncoder);
//        //id we want to use CompromisedPasswordChecker if we use oob DaoAuthenticationProvider it already has this line
////        auth.setCompromisedPasswordChecker(compromisedPasswordChecker);
//        return auth;
//    }


    //for the Authentication events to trigger we need to mention this
    @Bean
    public AuthenticationEventPublisher authenticationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {

        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    //for the Authorization events to trigger we need to mention this
    @Bean
    public AuthorizationEventPublisher authorizationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {

        return new SpringAuthorizationEventPublisher(applicationEventPublisher);
    }

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-uri}")
    String introspectionUri;

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-id}")
    String clientId;

    @Value("${spring.security.oauth2.resourceserver.opaque.introspection-client-secret}")
    String clientSecret;

    @PostConstruct
    public void debugPrint() {
        System.out.println("RESOLVED CLIENT ID: [" + clientId + "]");
        System.out.println("RESOLVED SECRET (last 4): ..." + clientSecret.substring(clientSecret.length() - 4));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    {
        //to get roles from keycloak as Granted Authorities
//        JwtAuthenticationConverter jwtAuthenticationConverter=new JwtAuthenticationConverter();
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());

        httpSecurity.cors(cors->cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration=new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setExposedHeaders(Arrays.asList("Authorization"));
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    }
                }))
                .authorizeHttpRequests(configure->
                configure.requestMatchers("/").hasRole("ADMIN")
                        .requestMatchers("/employee").hasRole("EMPLOYEE")
                        .requestMatchers("/myAccount/**").hasRole("USER")
                        .requestMatchers("/myBalance/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/myLoans/**").authenticated()//we do method level authorization
                        .requestMatchers("/myCards/**").hasRole("USER")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices","/contact","/error","/register").permitAll()
        );

        //this is to make every call https and not http the default port of https is 8443
//        httpSecurity.redirectToHttps(Customizer.withDefaults());

        //making this backend as resourse server with jwt token
//         httpSecurity.oauth2ResourceServer(rsc->rsc.jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter)));

        //making this backend as resourse server with opaque token
        httpSecurity.oauth2ResourceServer(rsc -> rsc.opaqueToken(otc -> otc.authenticationConverter(new KeycloakOpaqueRoleConverter())
                .introspectionUri(this.introspectionUri).introspectionClientCredentials(this.clientId,this.clientSecret)));

        //access denied handler we can only mention globaly not in httpBasic
        httpSecurity.exceptionHandling(exception->exception.accessDeniedHandler(new CustomAccessDeniedHandler()));


        //to redirect to this page if session expires. How long session should stay before expiration is given in application property file
        //Also we can set the limit of session we can create (by default unlimited) and also if session creation reached limit keep already active alive and reject those are going to be created
       // httpSecurity.sessionManagement(session->session.invalidSessionUrl("/invalidSession").maximumSessions(3).maxSessionsPreventsLogin(true));

        httpSecurity.sessionManagement(session->
               session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //if we comment it out it means its enabled
        //httpSecurity.csrf(csrf->csrf.disable());
        httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers( "/contact","/register")
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
                     .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);


        return httpSecurity.build();
    }

}
