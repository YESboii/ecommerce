package com.ayush.ayush.security;

import com.ayush.ayush.exceptions.JwtException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        String msg = resolveExceptionMessage(authException);

        response.getWriter().write(msg);
    }

    private String resolveExceptionMessage(AuthenticationException authException) {
        if (authException instanceof DisabledException) {
            return "Please, activate your account!";
        } else if (authException instanceof InsufficientAuthenticationException) {
            return "Invalid Credentials";
        } else if (authException instanceof JwtException) {
            return handleJwtException(authException.getCause());
        } else {
            return "Unauthorized";
        }
    }

    private String handleJwtException(Throwable cause) {
        if (cause instanceof ExpiredJwtException) {
            return "Token [ %s ] expired, login again".formatted(cause.getMessage());
        } else {
            return "Token [ %s ] is invalid/malformed".formatted(cause.getMessage());
        }
    }

}
