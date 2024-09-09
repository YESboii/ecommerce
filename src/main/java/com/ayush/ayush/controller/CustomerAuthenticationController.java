package com.ayush.ayush.controller;

import com.ayush.ayush.dto.AuthenticationRequest;
import com.ayush.ayush.dto.AuthenticationResponse;
import com.ayush.ayush.dto.CustomerRegistrationRequest;
import com.ayush.ayush.service.CustomerAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/customer")
@RequiredArgsConstructor
public class CustomerAuthenticationController {

    private final CustomerAuthenticationService authenticationService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> register(@RequestBody CustomerRegistrationRequest request){
        authenticationService.register(request);
        return new ResponseEntity<>("Registration Successful", HttpStatus.CREATED);
    }

    @PostMapping(value = "/authenticate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuthenticationResponse> authenticationResponse(@RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PatchMapping("/activate/{regId}")
    public ResponseEntity<String> register(@PathVariable String regId){
        boolean resultOfActivation = authenticationService.activate(regId);
        if (resultOfActivation){
            return ResponseEntity.ok("User successfully Activated");
        }
        return ResponseEntity.badRequest().body("User is already activated");
    }

}
