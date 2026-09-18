package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.entity.User;
import org.springframework.web.bind.WebDataBinder;

public interface UserDAO {

    public User findUserByUsername(String name);
    public User save(User user);

}
