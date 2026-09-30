package com.eazybytes.authServerCustom.repository;

import com.eazybytes.authServerCustom.model.User;

public interface userDao {
    public User findUserByUsername(String name);
}
