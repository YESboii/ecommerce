package com.ayush.ayush.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryDto {
    private int id;
    private String name;

    public CategoryDto(int id, String name) {
        this.id =id;
        this.name = name;
    }
}
