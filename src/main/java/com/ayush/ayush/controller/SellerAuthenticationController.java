package com.ayush.ayush.controller;

import com.ayush.ayush.dto.*;
import com.ayush.ayush.service.OtpService;
import com.ayush.ayush.service.SellerAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/seller")
@RequiredArgsConstructor
public class SellerAuthenticationController {

    private final SellerAuthenticationService authenticationService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<String> registerSeller(@RequestBody SellerRegistrationRequest request){
        authenticationService.register(request);
        return ResponseEntity.ok("Registration successful!!");
    }
    @PostMapping(value = "/authenticate",consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping("/generate-otp")
    public ResponseEntity<ObjectNode> generateOtp(@RequestBody @Valid GenerateOtpRequest request){
        String email = request.email();
        String key = otpService.sendOtpSeller(email);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("key", key);
        return ResponseEntity.ok(node);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody PasswordChangeRequest passwordChangeRequest){
        boolean isValidOtp = otpService.verifyOtp(passwordChangeRequest.key(), passwordChangeRequest.otp());
        if (isValidOtp){
            authenticationService.changePassword(passwordChangeRequest.key(), passwordChangeRequest.newPassword());
            return ResponseEntity.ok("Password Changed");
        }
        return ResponseEntity.badRequest().body("Invalid Otp");
    }

}
