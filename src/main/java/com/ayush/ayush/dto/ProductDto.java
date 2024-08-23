package com.ayush.ayush.dto;


import com.ayush.ayush.model.ProductCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {



    @NotBlank
    private String name;

    private String description;

    @Min(value = 1,message = "quantity must at least be 1")
    private int quantity;

    @DecimalMin(value = "0.0",inclusive = false, message = "price cannot be zero")
    private double amount;

    private byte[] image;

    @Size(min = 1, message = "must at least belong to one category")
    private List<CategoryDto> categories;
}
