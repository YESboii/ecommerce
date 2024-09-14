package com.ayush.ayush.controller;

import com.ayush.ayush.dto.*;
import com.ayush.ayush.service.CustomerAuthenticationService;
import com.ayush.ayush.service.OtpService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
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
    private final OtpService otpService;

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
    @PostMapping("/generate-otp")
    public ResponseEntity<ObjectNode> generateOtp(@RequestBody @Valid GenerateOtpRequest request){
        String email = request.email();
        String key = otpService.sendOtpCustomer(email);
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode node = objectMapper.createObjectNode();
        node.put("key", key);
        return ResponseEntity.ok(node);
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> verifyOtp(@Valid @RequestBody PasswordChangeRequestCustomer passwordChangeRequest){
        boolean isValidOtp = otpService.verifyOtp(passwordChangeRequest.key(), passwordChangeRequest.otp());
        if (isValidOtp){
            authenticationService.changePassword(passwordChangeRequest.key(), passwordChangeRequest.newPassword());
            return ResponseEntity.ok("Password Changed");
        }
        return ResponseEntity.badRequest().body("Invalid Otp");
    }

}
