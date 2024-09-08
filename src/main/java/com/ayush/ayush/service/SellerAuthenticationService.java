package com.ayush.ayush.service;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.AuthenticationResponse;
import com.ayush.ayush.dto.SellerRegistrationRequest;
import com.ayush.ayush.exceptions.RegistrationLinkExpiredException;
import com.ayush.ayush.exceptions.UserAlreadyExistsException;
import com.ayush.ayush.mapper.SellerRequestMapper;
import com.ayush.ayush.model.*;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.model.embeddedable.TokenType;
import com.ayush.ayush.repository.SellerActivationRepository;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import com.ayush.ayush.security.SellerAuthenticationToken;
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
public class SellerAuthenticationService implements AuthenticationService{

    private final EmailService emailService;
    private final SellerRepositoryJpa sellerRepository;
    private final TransactionTemplate transactionTemplate;
    private final SellerActivationRepository sellerActivationRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public SellerAuthenticationService(@Qualifier("sellerEmailService")EmailService emailService,
                                       SellerRepositoryJpa sellerRepository,
                                       PlatformTransactionManager platformTransactionManager,
                                       SellerActivationRepository sellerActivationRepository,
                                       PasswordEncoder passwordEncoder,
                                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.emailService = emailService;
        this.sellerRepository = sellerRepository;
        this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
        this.sellerActivationRepository = sellerActivationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    //1- Find Seller By email
    //If Found user already exists, throw exception, and notify client that email exists try to check email/ generate
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
    public AuthenticationResponse authenticate(AuthenticationRequest request){
        Authentication authentication = authenticationManager.authenticate(
                new SellerAuthenticationToken(request.username(), request.password())
        );
        Seller seller = (Seller) authentication.getPrincipal();
        jwtService.revokeAllTokensForSeller(seller);

        String accessToken = jwtService.generateAccessJwtSeller(seller);
        String refreshToken = jwtService.generateRefreshJwtSeller(seller);

        TokenSeller access = getToken(accessToken,seller,TokenType.ACCESS);
        TokenSeller refresh = getToken(refreshToken,seller,TokenType.REFRESH);

        jwtService.saveAllTokensSeller(List.of(access, refresh));

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    @Override
    public boolean activate(String registrationKey) {
        SellerActivation sellerActivation = sellerActivationRepository.findByKey(registrationKey).orElseThrow(
                () -> new UsernameNotFoundException("User does not exist, Invalid registration key!!")
        );
        if (sellerActivation.getExpiryTime().isBefore(LocalDateTime.now())){
            throw new RegistrationLinkExpiredException("The Registration link has expired!");
        }
        Seller seller = sellerActivation.getSeller();
        if(!seller.isEnabled()){
            seller.setEnabled(true);
            sellerRepository.save(seller);
            return true;
        }
        return false;
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
    private TokenSeller getToken(String jwt, Seller seller, TokenType tokenType){
        return TokenSeller.builder()
                .jwt(jwt)
                .isRevoked(false)
                .tokenType(tokenType)
                .seller(seller)
                .build();
    }
}
