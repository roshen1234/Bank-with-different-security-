package com.eazybytes.springsecsection1.security;

import com.eazybytes.springsecsection1.service.UserService;
import org.jspecify.annotations.Nullable;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

//this is the custom authenticationProvider that we are creating like default DaoAuthenticationProvider that spring has given
@Component
//because we mentioned this it runs only if profile is prod
@Profile("prod")
public class ProdUserNamePwdAuthenticationProvider implements AuthenticationProvider {

    public UserService userService;
    public PasswordEncoder passwordEncoder;

    public ProdUserNamePwdAuthenticationProvider(UserService userService, PasswordEncoder passwordEncoder)
    {
        this.userService=userService;
        this.passwordEncoder=passwordEncoder;
    }

    //authentication logic we need to mention here
    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
     String userName=authentication.getName();
     String pwd=authentication.getCredentials().toString();
     UserDetails userDetails=userService.loadUserByUsername(userName);
     if(passwordEncoder.matches(pwd,userDetails.getPassword()))
     {
         //here we can add any cutom check like only authentiate if age >30 so all those we can add here
         return new UsernamePasswordAuthenticationToken(userName,pwd,userDetails.getAuthorities());
     }
     else
     {
         throw new BadCredentialsException("Invalid Password");
     }
    }

    //this function define the type of object this custom Authentication provider accepts we need to mention here (here we mention the same type of DaoAuthenticationProvider but we can mention differnt types)
    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
