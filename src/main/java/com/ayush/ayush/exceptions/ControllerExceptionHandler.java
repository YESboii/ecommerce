package com.ayush.ayush.exceptions;

import com.ayush.ayush.exceptions.response.GeneralErrorResponse;
import com.ayush.ayush.exceptions.response.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationException(MethodArgumentNotValidException exception,HttpServletRequest request){
        Map<String, String> errorMap = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(
                (error) ->{
                    String field = ((FieldError)error).getField();
                    String errorMsg = error.getDefaultMessage();
                    errorMap.put(field, errorMsg);
                }
        );
        ValidationErrorResponse response = ValidationErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .validationErrors(errorMap)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);

    }
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<GeneralErrorResponse> handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest request){
        GeneralErrorResponse response = GeneralErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .build();
        return new ResponseEntity<>(response,HttpStatus.BAD_REQUEST);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<GeneralErrorResponse> handleServerErrorException(HttpServletRequest request){
//        GeneralErrorResponse response = GeneralErrorResponse.builder()
//                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
//                .message("Internal Server Error: Unexpected")
//                .path(request.getRequestURI())
//                .timestamp(LocalDateTime.now())
//                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
//                .build();
//        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
//    }
    @ExceptionHandler(StorageException.class)
    public ResponseEntity<String> handlee(Exception e ){
        return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
