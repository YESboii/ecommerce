package com.ayush.ayush.service;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.SellerRegistrationRequest;
import com.ayush.ayush.exceptions.UserAlreadyExistsException;
import com.ayush.ayush.mapper.SellerRequestMapper;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.SellerActivation;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.repository.SellerActivationRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class SellerAuthenticationService implements AuthenticationService{

    private final EmailService emailService;
    private final SellerRepositoryJpa sellerRepository;
    private final TransactionTemplate transactionTemplate;
    private final SellerActivationRepository sellerActivationRepository;
    private final PasswordEncoder passwordEncoder;

    public SellerAuthenticationService(@Qualifier("sellerEmailService")EmailService emailService,
                                       SellerRepositoryJpa sellerRepository,
                                       PlatformTransactionManager platformTransactionManager,
                                       SellerActivationRepository sellerActivationRepository,
                                       PasswordEncoder passwordEncoder
                                        ) {
        this.emailService = emailService;
        this.sellerRepository = sellerRepository;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.sellerActivationRepository = sellerActivationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //1- Find Seller By email
    //If Found user already exists, throw exception, and notify client that email exists try to  generate
    //new key
    public void register(SellerRegistrationRequest request) {
        Optional<Seller> s = sellerRepository.findByUsername(request.getUsername());
        if (s.isPresent()){
            throw new UserAlreadyExistsException("User is already registered");
        }
        Seller seller = getSeller(request);
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        SellerActivation activation = getActivation(seller);
        transactionTemplate.execute(
                new TransactionCallbackWithoutResult(){
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        try {
                            sellerRepository.save(seller);
                            sellerActivationRepository.save(activation);

                        } catch (Exception e) {
                            status.setRollbackOnly();
                        }
                    }
                }
        );
        emailService.sendRegistrationMessage(activation.getKey(), seller.getUsername());
    }

    @Override
    public void authenticate(AuthenticationRequest request) {
    }

    @Override
    public void activate(String registrationKey) {
    }
    private String generateRegistrationKey(){
        return UUID.randomUUID().toString();
    }
    private  Seller getSeller(SellerRegistrationRequest request) {
        Seller seller = SellerRequestMapper.mapToSeller(request);
        seller.setRole(Role.SELLER);
        seller.setEnabled(false);
        seller.setTwoFactorEnabled(false);
        return seller;
    }
    private  SellerActivation getActivation(Seller seller){
        return SellerActivation.builder()
                .key(generateRegistrationKey())
                .seller(seller)
                .expiryTime(LocalDateTime.now().plusHours(1L))
                .build();
    }
}
