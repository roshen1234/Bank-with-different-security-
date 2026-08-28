package com.eazybytes.springsecsection1.controller;

import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.entity.User;
import com.eazybytes.springsecsection1.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class UserController {

    UserService userService;

    public UserController(UserService userService)
    {
        this.userService=userService;
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

}
