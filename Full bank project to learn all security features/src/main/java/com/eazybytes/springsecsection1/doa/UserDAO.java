package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.User;

public interface UserDAO {

    public User findUserByUsername(String name);

}
