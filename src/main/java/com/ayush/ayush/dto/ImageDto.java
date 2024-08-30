package com.ayush.ayush.dto;

import org.springframework.http.MediaType;

public record ImageDto (byte []imgAsBytes, MediaType mediaType){
}
