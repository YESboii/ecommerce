package com.ayush.ayush.controller;

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



}
