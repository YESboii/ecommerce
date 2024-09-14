package com.ayush.ayush.service;


import com.ayush.ayush.exceptions.UserDoesNotExistsException;
import com.ayush.ayush.model.Customer;
import com.ayush.ayush.model.Seller;
import com.ayush.ayush.repository.CustomerRepository;
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
    private final EmailService emailServiceSeller;

    private final CustomerRepository customerRepository;
    private final EmailService emailServiceCustomer;

    private final String ERROR_MESSAGE_EMAIL = "Email does not exists!!";

    private static final Duration TTL = Duration.ofMinutes(5);
    public OtpService(StringRedisTemplate template,
                      SellerRepositoryJpa sellerRepository,
                      @Qualifier("sellerEmailService")EmailService emailServiceSeller,
                      CustomerRepository customerRepository,
                      @Qualifier("customerEmailService")EmailService emailServiceCustomer) {
        this.template = template;
        this.sellerRepository = sellerRepository;
        this.emailServiceSeller = emailServiceSeller;
        this.customerRepository = customerRepository;
        this.emailServiceCustomer = emailServiceCustomer;
    }

    public String sendOtpSeller(String username){
        //Extract only the id
        Long id = sellerRepository.findIdByUsername(username).orElseThrow(
                () -> new UserDoesNotExistsException(ERROR_MESSAGE_EMAIL)
        );
        String otp = generateOtp();
        String key = getKeySeller(id);
        template.opsForValue().set(key, otp, TTL);
        emailServiceSeller.sendOtpMessage(otp, username);

        return key;
    }
    public String sendOtpCustomer(String username){

        Long id = customerRepository.findIdByUsername(username).orElseThrow(
                () -> new UserDoesNotExistsException(ERROR_MESSAGE_EMAIL)
        );
        String otp = generateOtp();
        String key = getKeyCustomer(id);
        template.opsForValue().set(key, otp, TTL);
        emailServiceCustomer.sendOtpMessage(otp, username);

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
    private String getKeySeller(Long sellerId){
        return "Sotp:%s".formatted(sellerId);
    }
    private String getKeyCustomer(Long customerId){
        return "Cotp:%s".formatted(customerId);
    }
}