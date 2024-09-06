package com.ayush.ayush.service;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.RegistrationRequest;

public interface AuthenticationService {
    //implement Register in the impl classes as both have diff params

    void authenticate(AuthenticationRequest request);
    void activate(String registrationKey);
}
