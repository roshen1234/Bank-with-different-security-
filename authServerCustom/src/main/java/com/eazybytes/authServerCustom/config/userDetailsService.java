package com.eazybytes.authServerCustom.config;


import com.eazybytes.authServerCustom.model.User;
import com.eazybytes.authServerCustom.repository.userDao;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class userDetailsService implements UserDetailsService {

        private final userDao userDao;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            User user = userDao.findUserByUsername(username);

            if(user==null)
                throw new  UsernameNotFoundException("User details not found for the user: " + username);

            List<GrantedAuthority> authorities = user.getRoles().stream().map(authority -> new
                    SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(user.getUserName(),user.getPassword(),authorities);
        }

}
