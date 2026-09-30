package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.DTO.LoginRequestDTO;
import com.eazybytes.springsecsection1.DTO.LoginResponseDTO;
import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.constant.ApplicationConstants;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.doa.UserDaoImpl;
import com.eazybytes.springsecsection1.entity.User;
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
import java.util.stream.Collectors;

@RestController
public class UserController {

    UserDaoImpl userDao;
    public UserController(UserDaoImpl userDao)
    {

        this.userDao=userDao;
    }


    @GetMapping("/user")
    public User getUserDetailsAfterLogin(Authentication authetication)
    {
        User user=userDao.findUserByUsername(authetication.getName());
        return user;
    }

}
