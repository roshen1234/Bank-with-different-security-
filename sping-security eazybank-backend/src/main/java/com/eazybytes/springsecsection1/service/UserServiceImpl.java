package com.eazybytes.springsecsection1.service;

import com.eazybytes.springsecsection1.DTO.WebUser;
import com.eazybytes.springsecsection1.doa.RoleDOA;
import com.eazybytes.springsecsection1.doa.UserDAO;
import com.eazybytes.springsecsection1.entity.Role;
import com.eazybytes.springsecsection1.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{

    UserDAO userDAO;
    RoleDOA roleDOA;
    PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDAO userDAO, RoleDOA roleDOA,PasswordEncoder passwordEncoder)
    {
        this.userDAO=userDAO;
        this.roleDOA=roleDOA;
        this.passwordEncoder=passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user=userDAO.findUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }

        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(),
                mapRolesToAuthorities(user.getRoles()));
    }

    public Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles)
    {
       return  roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    @Override
    public int save(WebUser user) {
        User finalUser=new User();
        finalUser.setUserName(user.getUserName());
        finalUser.setEmail(user.getEmail());
        finalUser.setFirstName(user.getFirstName());
        finalUser.setLastName(user.getLastName());
        finalUser.setEnabled(true);
        finalUser.setMobileNumber(user.getMobileNumber());
        finalUser.setDate(new Date());
        finalUser.setPassword(passwordEncoder.encode(user.getPassword()));
        finalUser.setRoles(Arrays.asList(
                roleDOA.findRoleByName("VIEWACCOUNT"),
                roleDOA.findRoleByName("VIEWBALANCE")
        ));

        User theUser=userDAO.save(finalUser);

        return theUser.getId();
    }

    @Override
    public User findUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }


}
