package com.ayush.ayush.service;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.AuthenticationResponse;
import com.ayush.ayush.dto.CustomerRegistrationRequest;
import com.ayush.ayush.exceptions.RegistrationLinkExpiredException;
import com.ayush.ayush.exceptions.UserAlreadyExistsException;
import com.ayush.ayush.exceptions.UserDoesNotExistsException;
import com.ayush.ayush.mapper.CustomerMapper;
import com.ayush.ayush.model.*;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.model.embeddedable.TokenType;
import com.ayush.ayush.repository.CustomerActivationRepository;
import com.ayush.ayush.repository.CustomerRepository;
import com.ayush.ayush.security.CustomerAuthenticationToken;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerAuthenticationService implements AuthenticationService{

    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final CustomerRepository customerRepository;
    private final CustomerActivationRepository customerActivationRepository;

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TransactionTemplate transactionTemplate;

    public CustomerAuthenticationService(AuthenticationManager authenticationManager,
                                         @Qualifier("customerEmailService") EmailService emailService,
                                         CustomerRepository customerRepository,
                                         CustomerActivationRepository customerActivationRepository,
                                         JwtService jwtService,
                                         PlatformTransactionManager transactionManager,
                                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.customerRepository = customerRepository;
        this.customerActivationRepository = customerActivationRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }



    public void register(CustomerRegistrationRequest request){
        Optional<Customer> c = customerRepository.findByUsername(request.getUsername());
        if (c.isPresent()){
            throw new UserAlreadyExistsException("User is already registered");
        }
        Customer customer = getCustomer(request);
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        CustomerActivation activation = getActivation(customer);
        transactionTemplate.execute(
                new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
                            customerRepository.save(customer);
                            customerActivationRepository.save(activation);
                        } catch (Exception e) {
                            status.setRollbackOnly();
                        }
                    }
                }
        );
        emailService.sendRegistrationMessage(activation.getKey(), customer.getUsername());
    }
    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new CustomerAuthenticationToken(request.username(), request.password())
        );
        Customer customer = (Customer) (authentication.getPrincipal());
        jwtService.revokeAllTokenForCustomer(customer);
        String access = jwtService.generateAccessJwtCustomer(customer);
        String refresh = jwtService.generateRefreshJwtCustomer(customer);
        TokenCustomer accessToken = getToken(access,customer,TokenType.ACCESS);
        TokenCustomer refreshToken = getToken(refresh,customer,TokenType.REFRESH);

        jwtService.saveAllTokensCustomer(List.of(accessToken,refreshToken));

        return new AuthenticationResponse(access, refresh);
    }

    @Override
    public boolean activate(String registrationKey) {
        CustomerActivation customerActivation = customerActivationRepository.findByKey(registrationKey).orElseThrow(
                () -> new UserDoesNotExistsException("User does not exist, Invalid registration key!!")
        );
        if (customerActivation.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RegistrationLinkExpiredException("The Registration link has expired!");
        }
        Customer customer = customerActivation.getCustomer();
        if(!customer.isEnabled()){
            customer.setEnabled(true);
            customerRepository.save(customer);
            return true;
        }
        return false;
    }
    private TokenCustomer getToken(String jwt, Customer customer, TokenType tokenType){
        return TokenCustomer.builder()
                .jwt(jwt)
                .isRevoked(false)
                .tokenType(tokenType)
                .customer(customer)
                .build();
    }

    private CustomerActivation getActivation(Customer customer) {
        return CustomerActivation.builder()
                .key(generateRegistrationKey())
                .customer(customer)
                .expiryTime(LocalDateTime.now().plusHours(1L))
                .build();
    }
    private String generateRegistrationKey(){
        return UUID.randomUUID().toString();
    }

    private Customer getCustomer(CustomerRegistrationRequest request) {
        Customer customer = CustomerMapper.toEntity(request);
        customer.setRole(Role.CUSTOMER);
        customer.setEnabled(false);
        customer.setTwoFactorEnabled(false);

        return customer;
    }

}
