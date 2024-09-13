package com.ayush.ayush.exceptions;

public class UserDoesNotExistsException extends RuntimeException{

    public UserDoesNotExistsException() {
    }

    public UserDoesNotExistsException(String message) {
        super(message);
    }

    public UserDoesNotExistsException(String message, Throwable cause) {
        super(message, cause);
    }

}
