package com.eazybytes.springsecsection1.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.LocalDateTime;

//here we create a custom exception handler which will override default BasicAuthenticationEntryPoint which throws errors when we enter wrong credential etc (only when we login using basic authentication this trigger)
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        LocalDateTime currentTimeStamp= LocalDateTime.now();
        String message=(authException!=null && authException.getMessage()!=null)?authException.getMessage():"Unauthorized";
        String path=request.getRequestURI();

        response.setHeader("Error-reason", "Authentication failed");
        //the below line will return default response when error happens
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());

        //below lines is to set custom response message
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        String jsonReponse=
                String.format("{\"timestamp\":\"%s\",\"status\":\"%d\",\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                        currentTimeStamp,HttpStatus.UNAUTHORIZED.value(),HttpStatus.UNAUTHORIZED.getReasonPhrase(),message,path);
        response.getWriter().write(jsonReponse);
    }
}
