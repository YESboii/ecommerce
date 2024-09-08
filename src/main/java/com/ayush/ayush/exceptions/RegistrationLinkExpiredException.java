package com.ayush.ayush.exceptions;

public class RegistrationLinkExpiredException  extends RuntimeException{
    public RegistrationLinkExpiredException(String message){
        super(message);
    }
}
