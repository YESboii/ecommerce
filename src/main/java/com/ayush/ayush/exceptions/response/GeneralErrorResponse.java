package com.ayush.ayush.exceptions.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public final class GeneralErrorResponse {

    private LocalDateTime timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

}
