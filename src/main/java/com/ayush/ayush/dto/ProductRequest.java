package com.ayush.ayush.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductRequest {

    @NotBlank(message = "Required. Cannot be blank")
    private String name;

    @NotBlank(message = "Required. Cannot be blank")
    private String description;

    @Min(value = 1,message = "Quantity must at least be 1")
    @NotNull(message = "Required field!")
    private Integer quantity;

    @DecimalMin(value = "0.0",inclusive = false, message = "Price cannot be zero")
    @NotNull(message = "Required field!")
    private Double amount;


    @Size(min = 1, message = "must at least belong to one category")
    @NotNull(message = "Required field!")
    private List<CategoryDto> categories;
}
