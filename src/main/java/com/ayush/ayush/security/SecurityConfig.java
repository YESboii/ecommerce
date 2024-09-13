package com.ayush.ayush.security;

import com.ayush.ayush.model.embeddedable.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.context.NullSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfFilter;

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
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter authenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)throws Exception {
        return httpSecurity
//                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/api/*/seller/**").hasAuthority(Role.SELLER.getAuthority())
                                .requestMatchers("/api/*/customer/**").hasAuthority(Role.CUSTOMER.getAuthority())
                                .anyRequest().authenticated()
                        )
                .addFilterBefore(authenticationFilter, AuthorizationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .securityContext(context -> context.securityContextRepository(new NullSecurityContextRepository()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler()))
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

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint(){
        return new CustomAuthenticationEntryPoint();
    }
    @Bean
    public AccessDeniedHandler customAccessDeniedHandler(){
        return new CustomAccessDeniedHandlerImpl();
    }
}
