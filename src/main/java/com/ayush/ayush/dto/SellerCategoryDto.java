package com.ayush.ayush.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerCategoryDto {

    private int id;
    private String name;

    public SellerCategoryDto(int id, String name) {
        this.id =id;
        this.name = name;
    }
}