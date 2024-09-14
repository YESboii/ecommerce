package com.ayush.ayush.service;


import com.ayush.ayush.exceptions.UserDoesNotExistsException;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.repository.SellerRepositoryJpa;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;

@Service
public class OtpService {

    private final StringRedisTemplate template;
    private final SellerRepositoryJpa sellerRepository;
    private final EmailService emailService;

    private static final Duration TTL = Duration.ofMinutes(5);
    public OtpService(StringRedisTemplate template,
                      SellerRepositoryJpa sellerRepository,
                        @Qualifier("sellerEmailService")EmailService emailService) {
        this.template = template;
        this.sellerRepository = sellerRepository;
        this.emailService = emailService;
    }

    public String sendOtpSeller(String username){
        //Extract only the id
        Seller seller = sellerRepository.findByUsername(username).orElseThrow(
                () -> new UserDoesNotExistsException("Email does not exists!!")
        );
        String otp = generateOtp();
        String key = getKey(seller.getId());
        template.opsForValue().set(key, otp, TTL);
        emailService.sendOtpMessage(otp, seller.getUsername());

        return key;
    }
    public boolean verifyOtp(String key, String otp){
        if(key==null || otp == null){
            return false;
        }
        String expectedOtp = template.opsForValue().get(key);

        if (expectedOtp==null) return false;

        boolean isValid =  expectedOtp.equals(otp);

        if (isValid){
            template.delete(key);
        }
        return isValid;
    }

    private String generateOtp(){
        SecureRandom secureRandom = new SecureRandom();
        int random = 100_000 + secureRandom.nextInt(900_000);
        return String.valueOf(random);
    }
    //In production, a more unpredictable identifier should be used instead of the id such as hash, uuid
    //so it is not easy to send some random request and change someone else's password
    //If the keyFormat changes also change the extractId function sellerAuthenticationController
    private String getKey(Long sellerId){
        return "Sotp:%s".formatted(sellerId);
    }
}