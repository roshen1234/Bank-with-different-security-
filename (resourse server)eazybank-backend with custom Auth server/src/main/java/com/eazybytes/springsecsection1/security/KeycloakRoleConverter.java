package com.eazybytes.springsecsection1.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        ArrayList<String> realmAccess=(ArrayList<String>)source.getClaims().get("roles");
        if(realmAccess==null && realmAccess.isEmpty())
        {
            return new ArrayList<>();
        }

        Collection<GrantedAuthority>returnValue=realmAccess
                .stream().map(roleName->"ROLE_"+roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return returnValue;
    }
}
