package com.ayush.ayush.security;

import com.ayush.ayush.repository.SellerRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("sellerUserDetailsService")
@RequiredArgsConstructor
public class SellerUserDetailsService implements UserDetailsService {

    private final SellerRepositoryJpa sellerRepositoryJpa;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return sellerRepositoryJpa.findByUsername(username).orElseThrow(()->
                new UsernameNotFoundException("Username not found")
                );
    }
}
