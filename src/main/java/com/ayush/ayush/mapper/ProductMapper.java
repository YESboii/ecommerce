package com.ayush.ayush.mapper;

import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.service.FileService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;


public class ProductMapper {
    FileService fileService;

    public static ProductResponse toDto(Product product,byte[] img) {
        if (product == null) {
            return null;
        }

//        ProductRequest dto = new ProductRequest();
//        dto.setName(product.getName());
//        dto.setDescription(product.getDescription());
//        dto.setQuantity(product.getQuantity());
//        dto.setAmount(product.getAmount());
//        dto.setCategories(CategoryMapper.toDto(product.getCategories()));
        ProductResponse dto = ProductResponse
                .builder()
                .id(product.getId())
                .name(product.getName())
                .amount(product.getAmount())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .categories(CategoryMapper.toDto(product.getCategories()))
                .img(img)
                .build();

        return dto;
    }

    public static Product toEntity(ProductRequest dto) {
        if (dto == null) {
            return null;
        }

        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setQuantity(dto.getQuantity());
        product.setAmount(dto.getAmount());
        product.setCategories(CategoryMapper.toEntity(dto.getCategories()));

        return product;
    }
}
