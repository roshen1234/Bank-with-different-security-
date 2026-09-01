package com.eazybytes.springsecsection1.security;

import com.eazybytes.springsecsection1.exception.CustomBasicAuthenticationEntryPoint;
import com.eazybytes.springsecsection1.filter.*;
import com.eazybytes.springsecsection1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.sql.DataSource;
import javax.xml.crypto.Data;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

@Configuration
@Profile("!prod")
public class DemoSecurityConfig {

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
        httpSecurity
                .cors(cors->cors.configurationSource(new CorsConfigurationSource() {
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
                configure.requestMatchers("/").hasRole("admin")
                        .requestMatchers("/employee").hasRole("EMPLOYEE")
                        .requestMatchers("/myAccount/**").hasRole("USER")
                        .requestMatchers("/myBalance/**").hasAnyRole("USER","ADMIN")
                        .requestMatchers("/myLoans/**").hasRole("USER")
                        .requestMatchers("/myCards/**").hasRole("USER")
                        .requestMatchers("/user").authenticated()
                        .requestMatchers("/notices","/contact","/error","/register","/invalidSession").permitAll()
        );

        httpSecurity.httpBasic(Customizer.withDefaults());

        httpSecurity.formLogin(Customizer.withDefaults());
        //httpSecurity.sessionManagement(session->session.invalidSessionUrl("/invalidSession").maximumSessions(3).maxSessionsPreventsLogin(true));

        httpSecurity.sessionManagement(session->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        //httpSecurity.csrf(csrf->csrf.disable());
        httpSecurity.csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers( "/contact","/register")
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
                    .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);

        httpSecurity.addFilterAt(new AuthoritiesLoggingAtFilter(),BasicAuthenticationFilter.class)
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(),BasicAuthenticationFilter.class)
                .addFilterBefore(new RequestValidationBeforeFilter(),BasicAuthenticationFilter.class)
                .addFilterAfter(new JWTTokenGeneratorFilter(),BasicAuthenticationFilter.class)
                .addFilterBefore(new JWTTokenValidatorFilter(),BasicAuthenticationFilter.class);


        return httpSecurity.build();
    }

}
