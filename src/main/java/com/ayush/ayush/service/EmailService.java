package com.ayush.ayush.service;

public interface EmailService {

    void sendRegistrationMessage(String key,String sendToEmail);

    void sendOtpMessage(String otp, String username);
}
