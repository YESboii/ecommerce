package com.ayush.ayush.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
/*
* TO SOLVE THE PROBLEM OF AUTHENTICATING SELLER AND CUSTOMER FOR USING DIFFERENT USER_DETAILS_SERVICE
* WHAT WE CAN DO IS CREATE TWO SUBCLASSES OF USERNAME_PASSWORD_AUTHENTICATION_TOKEN
* ONE FOR SELLER AND ONE FOR CUSTOMER AND CREATE TWO SUBCLASSES OF DAO_AUTHENTICATION_PROVIDER
* FOR EACH AUTH_PROVIDER WE WILL SET DIFFERENT IMPLEMENTATIONS FOR USER_DETAILS_SERVICE
* ONE WILL SUPPORT THE TOKEN FOR SELLER AND OTHER FOR CUSTOMER
* AND ADD BOTH OF THESE PROVIDERS TO THE PROVIDER_MANAGER
* WHEN AUTHENTICATING THE SELLER ENDPOINT WE WILL CREATE A SELLER_AUTHENTICATION_TOKEN AND IN OUR CUSTOM
* AUTHENTICATION_PROVIDER WE WILL SIMPLY CAST THE TOKEN TO USERNAME_PASSWORD_AUTHENTICATION_TOKEN AND CALL
* SUPER.AUTHENTICATE(TOKEN(CASTED_TO_UPA TOKEN)) AND SAME FOR CUSTOMER.
* */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
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
    public CustomerAuthenticationProvider customerAuthenticationProvider(@Qualifier("customerUserDetailsService")
                                                                             UserDetailsService userDetailsService){
        CustomerAuthenticationProvider customerAuthenticationProvider = new CustomerAuthenticationProvider();
        customerAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        customerAuthenticationProvider.setUserDetailsService(userDetailsService);

        return customerAuthenticationProvider;
    }
    @Bean
    public AuthenticationManager authenticationManager(SellerAuthenticationProvider sellerAuthenticationProvider,
                                                       CustomerAuthenticationProvider customerAuthenticationProvider){

        return new ProviderManager(sellerAuthenticationProvider, customerAuthenticationProvider);
    }
}
