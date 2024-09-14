package com.ayush.ayush.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordChangeRequestCustomer(
        @Pattern(regexp = "^Cotp:\\d+$", message = "The key is invalid")String key,
        @Pattern(regexp = "\\d{6}", message = "OTP must be a 6-digit number") String otp,
        @NotBlank String newPassword
) {
}
