package com.eazybytes.springsecsection1.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.LocalDateTime;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LocalDateTime currentTimeStamp= LocalDateTime.now();
        String message=(accessDeniedException!=null && accessDeniedException.getMessage()!=null)?accessDeniedException.getMessage():"Authorization Failed";
        String path=request.getRequestURI();

        response.setHeader("denied-reason", "Authorization failed");
        //the below line will return default response when error happens
//        response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());

        //below lines is to set custom response message
        response.setStatus(HttpStatus.FORBIDDEN.value());
        String jsonReponse=
                String.format("{\"timestamp\":\"%s\",\"status\":\"%d\",\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                        currentTimeStamp,HttpStatus.FORBIDDEN.value(),HttpStatus.FORBIDDEN.getReasonPhrase(),message,path);
        response.getWriter().write(jsonReponse);
    }
}
