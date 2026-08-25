package com.eazybytes.springsecsection1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
        httpSecurity.authorizeHttpRequests(configure->
                configure.requestMatchers("/").hasRole("ADMIN")
                        .requestMatchers("/employee").hasRole("EMPLOYEE")
                        .requestMatchers("/myAccount","/myBalance","/myLoans","/myCards").authenticated()
                        .requestMatchers("/notices","/contact","/error","/register").permitAll()
        );

        //this is to make every call https and not http the default port of https is 8443
        httpSecurity.redirectToHttps(Customizer.withDefaults());

        httpSecurity.httpBasic(Customizer.withDefaults());

//        httpSecurity.sessionManagement(session->
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        
        httpSecurity.csrf(csrf -> csrf.disable());

        return httpSecurity.build();
    }

}
