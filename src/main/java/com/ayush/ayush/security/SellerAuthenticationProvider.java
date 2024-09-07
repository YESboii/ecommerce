package com.ayush.ayush.security;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class SellerAuthenticationProvider extends DaoAuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        if(!(authentication instanceof SellerAuthenticationToken)){
            throw new IllegalArgumentException("invalid");
        }
        return super.authenticate(authentication);
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return (SellerAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
