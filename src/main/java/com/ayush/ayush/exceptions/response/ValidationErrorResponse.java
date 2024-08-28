package com.ayush.ayush.exceptions.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Builder
@Getter
@Setter
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private Map<String,String> validationErrors;
    private String path;
}
