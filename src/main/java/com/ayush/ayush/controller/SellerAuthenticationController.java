package com.ayush.ayush.controller;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.AuthenticationResponse;
import com.ayush.ayush.dto.SellerRegistrationRequest;
import com.ayush.ayush.service.SellerAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/seller")
@RequiredArgsConstructor
public class SellerAuthenticationController {

    private final SellerAuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerRegistrationRequest request){
        authenticationService.register(request);
        return ResponseEntity.ok("Registration successful!!");
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticateSeller(@RequestBody AuthenticationRequest request){
        AuthenticationResponse response = authenticationService.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/activate/{registrationKey}")
    public ResponseEntity<String> activateSeller(@PathVariable String registrationKey){
        boolean resultOfActivation = authenticationService.activate(registrationKey);
        if (resultOfActivation){
            return ResponseEntity.ok("User successfully Activated");
        }
        return ResponseEntity.badRequest().body("User is already activated");
    }



}
