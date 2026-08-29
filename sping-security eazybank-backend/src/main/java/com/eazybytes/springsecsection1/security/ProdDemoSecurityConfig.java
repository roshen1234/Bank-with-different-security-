package com.eazybytes.springsecsection1.security;

import com.eazybytes.springsecsection1.exception.CustomAccessDeniedHandler;
import com.eazybytes.springsecsection1.exception.CustomBasicAuthenticationEntryPoint;
import com.eazybytes.springsecsection1.filter.CsrfCookieFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@Profile("prod")
public class ProdDemoSecurityConfig {

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

    //we can use above code but maybe in future instead of bCrypt some other can come so if we use the below code it will always select the latest best one.But if we use this we have to mention in the password what we use like {bcrypt}{noop} etc
    @Bean
    public PasswordEncoder passwordEncoder()
    {

        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //this is to check if the password is good ot not the entered password if not they will need to select another password (if we add this then in default DaoAuthenticationProvider it checks this and return result)
//    @Bean
//    public CompromisedPasswordChecker compromisedPasswordChecker(){
//        return new HaveIBeenPwnedRestApiPasswordChecker();
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


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    {
        httpSecurity.cors(cors->cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public @Nullable CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration=new CorsConfiguration();
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
                        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
                        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    }
                }))
                .authorizeHttpRequests(configure->
                configure.requestMatchers("/").hasRole("ADMIN")
                        .requestMatchers("/employee").hasRole("EMPLOYEE")
                        .requestMatchers("/myAccount/**","/myBalance/**","/myLoans/**","/myCards/**","/user").authenticated()
                        .requestMatchers("/notices","/contact","/error","/register","/invalidSession").permitAll()
        );

        //this is to make every call https and not http the default port of https is 8443
//        httpSecurity.redirectToHttps(Customizer.withDefaults());

        //the below line will call BasicAuthenticationEntryPoint its default given by spring
//        httpSecurity.httpBasic(Customizer.withDefaults());
        //the below line will tell spring to call Custom BasicAuthenticationEntryPoint instead of default BasicAuthenticationEntryPoint this is triggered when there is error when we try to login (only for basic Authentication login error it will trigger)
        httpSecurity.httpBasic(hbc->hbc.authenticationEntryPoint((new CustomBasicAuthenticationEntryPoint())));
        //httpSecurity.exceptionHandling(exception->exception.authenticationEntryPoint(new CustomBasicAuthenticationEntryPoint()));//If we want to mention it as global then we can mention like this but mostly we will use this for only basic authentication so above is also fine

        //access denied handler we can only mention globaly not in httpBasic
        httpSecurity.exceptionHandling(exception->exception.accessDeniedHandler(new CustomAccessDeniedHandler()));

        httpSecurity.formLogin(Customizer.withDefaults());

        //to redirect to this page if session expires. How long session should stay before expiration is given in application property file
        //Also we can set the limit of session we can create (by default unlimited) and also if session creation reached limit keep already active alive and reject those are going to be created
        httpSecurity.sessionManagement(session->session.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true));

//        httpSecurity.sessionManagement(session->
//               session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //if we comment it out it means its enabled
        //httpSecurity.csrf(csrf->csrf.disable());
        httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        //.ignoringRequestMatchers( "/contact","/register")
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
                     .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        return httpSecurity.build();
    }

}
