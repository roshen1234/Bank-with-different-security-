package com.eazybytes.springsecsection1.doa;

import com.eazybytes.springsecsection1.entity.Role;

public interface RoleDOA {
    public Role findRoleByName(String theRoleName);
}
