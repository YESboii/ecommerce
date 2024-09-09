package com.ayush.ayush.security;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.access.ExceptionTranslationFilter;

public class CustomerAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(!(authentication instanceof CustomerAuthenticationToken)){
            throw new IllegalArgumentException("invalid");
        }
        return super.authenticate(authentication);
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return (CustomerAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
