package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.DTO.LoginRequestDTO;
import com.eazybytes.springsecsection1.DTO.LoginResponseDTO;
import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.constant.ApplicationConstants;
import com.eazybytes.springsecsection1.entity.User;
import com.eazybytes.springsecsection1.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    UserService userService;
    Environment env;
    AuthenticationManager authenticationManager;
    public UserController(UserService userService, Environment env, AuthenticationManager authenticationManager)
    {

        this.userService=userService;
        this.env=env;
        this.authenticationManager=authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody WebUser user) {
        try {
            User existingUser=userService.findUserByUsername(user.getUserName());

            if(existingUser!=null)
            {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("User already exist");
            }

            int userId = userService.save(user);

            if(userId>0)
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("User created with ID: " + userId);

            else {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("User creation failed");
            }
        } catch (Exception exc) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An Exception occurred"+exc);
        }
    }

    @GetMapping("/user")
    public User getUserDetailsAfterLogin(Authentication authetication)
    {
        User user=userService.findUserByUsername(authetication.getName());
        return user;
    }

    @PostMapping("/apiLogin")
    public ResponseEntity<LoginResponseDTO>apiLogin (@RequestBody LoginRequestDTO loginRequest)
    {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if(null != authenticationResponse && authenticationResponse.isAuthenticated()) {
            if (null != env) {
                String secret = env.getProperty(ApplicationConstants.JWT_SECRET_KEY,
                        ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);
                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = Jwts.builder().issuer("Eazy Bank").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream().map(
                                GrantedAuthority::getAuthority).collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date())
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }
        return ResponseEntity.status(HttpStatus.OK).header(ApplicationConstants.JWT_HEADER,jwt)
                .body(new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt));
    }

}
