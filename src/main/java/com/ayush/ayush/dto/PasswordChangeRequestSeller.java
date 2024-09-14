package com.ayush.ayush.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordChangeRequestSeller(

        @Pattern(regexp = "^Sotp:\\d+$", message = "The key is invalid")String key,
        @Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit number") String otp,
        @NotBlank String newPassword){}
