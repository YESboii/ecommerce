package com.ayush.ayush.security.customer;

import com.ayush.ayush.model.Customer;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.security.CustomerAuthenticationProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean("customerUserDetailsService")
    public UserDetailsService usd(){
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Ayush");
        customer.setUsername("xyz@gmail.com");
        customer.setEnabled(true);
        customer.setRole(Role.CUSTOMER);
        customer.setPassword(passwordEncoder().encode("12345"));
        customer.setTwoFactorEnabled(false);
        UserDetailsService userDetailsService
                = (username) ->{
            if (username.equals("xyz@gmail.com")){
                return customer;
            }
            throw new UsernameNotFoundException("");
        };
        return userDetailsService;
    }
    @Bean
    public CustomerAuthenticationProvider customerAuthenticationProvider(@Qualifier("customerUserDetailsService")
                                                                             UserDetailsService userDetailsService){
        CustomerAuthenticationProvider customerAuthenticationProvider = new CustomerAuthenticationProvider();
        customerAuthenticationProvider.setUserDetailsService(usd());
        customerAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return customerAuthenticationProvider;
    }
}
