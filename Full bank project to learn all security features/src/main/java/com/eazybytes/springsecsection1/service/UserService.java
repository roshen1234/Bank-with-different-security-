package com.eazybytes.springsecsection1.service;

import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    public int save(WebUser user);
    public User findUserByUsername(String username);
}
