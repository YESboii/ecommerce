package com.ayush.ayush.dto;

import com.ayush.ayush.model.ProductCategory;
import lombok.Builder;

import java.util.List;

@Builder
public record ProductResponse(
        Integer id,
        String name,
        String description,
        Integer quantity,
        Double amount,
        List<CategoryDto> categories,
        byte[] img
) {
}
