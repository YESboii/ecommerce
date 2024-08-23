package com.ayush.ayush.mapper;

import com.ayush.ayush.dto.ProductDto;
import com.ayush.ayush.model.Product;

public class ProductMapper {

    public static ProductDto toDto(Product product) {
        if (product == null) {
            return null;
        }

        ProductDto dto = new ProductDto();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setQuantity(product.getQuantity());
        dto.setAmount(product.getAmount());
        dto.setImage(product.getImage());
        dto.setCategories(CategoryMapper.toDto(product.getCategories()));

        return dto;
    }

    public static Product toEntity(ProductDto dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        product.setAmount(dto.getAmount());
        product.setImage(dto.getImage());
        product.setCategories(CategoryMapper.toEntity(dto.getCategories()));

        return product;
    }
}
