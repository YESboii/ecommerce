package com.ayush.ayush;

import com.ayush.ayush.dto.CategoryDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.mapper.ProductMapper;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.model.Seller;

import java.util.List;

public class Utils {


    public static final String PRODUCT_NAME = "Smartphone";
    public static Product buildProduct(){
        Seller seller = new Seller();
        seller.setId(2L);
        seller.setName("John's Store");

        String image = "image.jpg";

        // Build the Product object
        Product product = Product.builder()
                .id(4)
                .name("Smartphone")
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(699.99)
                .image(image)
                .seller(seller)
                .build();
        return product;
    }
    public static ProductResponse buildProductResponseWithImage(){
        CategoryDto category1 = new CategoryDto(1, "Electronics");
        CategoryDto category2 = new CategoryDto(2, "Gadgets");

        // Example image data
        byte[] imageBytes = new byte[]{};

        // Build the ProductResponse object
        ProductResponse productResponse = ProductResponse.builder()
                .id(4)
                .name("Smartphone")
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(699.99)
                .categories(List.of(category1, category2))
                .img(imageBytes)
                .build();
        return productResponse;
    }
    public static ProductResponse buildProductResponseWithoutImage(){
        CategoryDto category1 = new CategoryDto(1, "Electronics");
        CategoryDto category2 = new CategoryDto(2, "Gadgets");

        // Example image data
        byte[] imageBytes = new byte[]{};

        // Build the ProductResponse object
        ProductResponse productResponse = ProductResponse.builder()
                .id(4)
                .name("Smartphone")
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(699.99)
                .categories(List.of(category1, category2))
                .img(null)
                .build();
        return productResponse;
    }
    public static ProductRequest buildProductReq(){
        CategoryDto category1 = new CategoryDto(1, "Electronics");
        CategoryDto category2 = new CategoryDto(2, "Gadgets");
        return  ProductRequest.builder()
                .name("Smartphone")
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(699.99)
                .categories(List.of(category1, category2))
                .build();
    }
    public static Product productToBeSaved(){
        return ProductMapper.toEntity(buildProductReq());
    }
    public static Product savedProduct(){
         Product p = ProductMapper.toEntity(buildProductReq());
         p.setId(4);
         return  p;
    }
}
