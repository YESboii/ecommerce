package com.ayush.ayush.mapper;

import com.ayush.ayush.dto.CategoryDto;

import com.ayush.ayush.dto.SellerCategoryDto;
import com.ayush.ayush.model.ProductCategory;
import com.ayush.ayush.model.SellerCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class CategoryMapper {
    public static List<CategoryDto> toDto(List<ProductCategory> categoryList) {
        if (categoryList == null) {
            return null;
        }
        List<CategoryDto> dto = new ArrayList<>();
        for(ProductCategory category : categoryList) {
            CategoryDto d = new CategoryDto();
            d.setId(category.getId());
            d.setName(category.getName());
            dto.add(d);
        }

        return dto;
    }

    public static List<ProductCategory> toEntity(List<CategoryDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        List<ProductCategory> category = new ArrayList<>();
        for(CategoryDto dto : dtoList) {
            ProductCategory c = new ProductCategory();
            c.setId(dto.getId());
            c.setName(dto.getName());
            category.add(c);
        }

        return category;
    }
    public static List<SellerCategory> toSellerCategoryEntity(List<SellerCategoryDto> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream().map(dto -> {
            SellerCategory category = new SellerCategory();
            category.setId(dto.getId());
            category.setName(dto.getName());
            return category;
        }).collect(Collectors.toList());
    }
}
