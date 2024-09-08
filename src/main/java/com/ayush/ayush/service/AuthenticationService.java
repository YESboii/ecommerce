package com.ayush.ayush.service;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.AuthenticationResponse;
import com.ayush.ayush.dto.RegistrationRequest;

public interface AuthenticationService {
    //implement Register in the impl classes as both have diff params

    AuthenticationResponse authenticate(AuthenticationRequest request);
    boolean activate(String registrationKey);
}
