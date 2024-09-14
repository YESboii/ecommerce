package com.ayush.ayush.service;


import com.ayush.ayush.exceptions.EmailServiceException;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.MimeMessageHelper;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import org.springframework.transaction.annotation.Transactional;

import java.util.Properties;

//Would generally use a third party Service
public abstract class AbstractEmailService implements EmailService {
    @Value("${app.app-email}")
    private String appEmail;
    @Value("${app.app-password}")
    private String password;

    private JavaMailSenderImpl mailSender;

    @PostConstruct
    private void init(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.transport.protocol","smtp");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.debug", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.enable", "true");


        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(465);
        mailSender.setUsername(appEmail);
        mailSender.setPassword(password);
        this.mailSender = mailSender;
    }

    @Override
    @Transactional(propagation = NEVER)
    public void sendRegistrationMessage(String key,String receiverEmail){
        assertArguments(key, receiverEmail);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setTo(receiverEmail);
            helper.setSubject("Registration Link");
            String baseUri = getBasePath();
            helper.setText(baseUri + key);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailServiceException("Error Sending Email", e);
        }
    }
    public void sendOtpMessage(String otp,String receiverEmail){
       assertArguments(otp,receiverEmail);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
            helper.setTo(receiverEmail);
            helper.setSubject("Otp For Changing Password");
            helper.setText(otp);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new EmailServiceException("Error Sending Email", e);
        }
    }
    private void assertArguments(String arg, String email){
        if(email==null || arg==null || arg.trim().length()==0){
            throw new IllegalArgumentException("Arguments cannot be null or Blank!");
        }
    }
    abstract String getBasePath();
}
