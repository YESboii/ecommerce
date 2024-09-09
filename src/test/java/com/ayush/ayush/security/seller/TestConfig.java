package com.ayush.ayush.security.seller;

import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.security.SellerAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@TestConfiguration
public class TestConfig {

    @Bean("sellerUserDetailsService")
    public UserDetailsService inMemoryCustomerUserDetailsService() {

        Seller seller = new Seller();
        seller.setId(1L);
        seller.setUsername("xyz@gmail.com");
        seller.setPassword(passwordEncoder().encode("12345"));
        seller.setRole(Role.SELLER);
        seller.setEnabled(true);
        seller.setTwoFactorEnabled(false);
        seller.setName("Ayush");
        UserDetailsService manager = (username) -> {
            if (username.equals("xyz@gmail.com")){
                return seller;
            }
            throw new UsernameNotFoundException("");
        };

        return manager;
    }
    @Bean
    public SellerAuthenticationProvider sellerAuthenticationProvider(@Qualifier("sellerUserDetailsService")
                                                                     UserDetailsService userDetailsService
    ){
        SellerAuthenticationProvider sellerAuthenticationProvider = new SellerAuthenticationProvider();
        sellerAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        sellerAuthenticationProvider.setUserDetailsService(userDetailsService);
        return sellerAuthenticationProvider;
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
