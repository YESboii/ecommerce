package com.ayush.ayush;

import com.ayush.ayush.controller.response.ProductListResponse;
import com.ayush.ayush.dto.CategoryDto;
import com.ayush.ayush.dto.ProductRequest;
import com.ayush.ayush.dto.ProductResponse;
import com.ayush.ayush.mapper.ProductMapper;
import com.ayush.ayush.model.Product;
import com.ayush.ayush.model.Seller;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.util.ArrayList;
import java.util.Arrays;
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
        byte[] imageBytes = new byte[]{1};

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

    public static List<ProductResponse> getProductListResponse() {
        List<ProductResponse> list = new ArrayList<>();

        // Adding product responses dynamically
        list.add(buildProductResponseWithImageId(1,1.11,"a"));
        list.add(buildProductResponseWithImageId(2,2.22,"b"));
        list.add(buildProductResponseWithoutImageId(3,3.33,"c"));

        return list;  // Returning the dynamic list
    }
    public static ProductResponse buildProductResponseWithImageId(Integer id,Double p,String n){
        CategoryDto category1 = new CategoryDto(1, "Electronics");
        CategoryDto category2 = new CategoryDto(2, "Gadgets");

        // Example image data
        byte[] imageBytes = new byte[]{1};

        // Build the ProductResponse object
        ProductResponse productResponse = ProductResponse.builder()
                .id(id)
                .name(n)
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(p)
                .categories(List.of(category1, category2))
                .img(imageBytes)
                .build();
        return productResponse;
    }
    public static ProductResponse buildProductResponseWithoutImageId(Integer id,Double p , String n){
        CategoryDto category1 = new CategoryDto(1, "Electronics");
        CategoryDto category2 = new CategoryDto(2, "Gadgets");

        // Example image data
        byte[] imageBytes = new byte[]{};

        // Build the ProductResponse object
        ProductResponse productResponse = ProductResponse.builder()
                .id(id)
                .name(n)
                .description("Latest model with high-end features")
                .quantity(50)
                .amount(p)
                .categories(List.of(category1, category2))
                .img(null)
                .build();
        return productResponse;
    }
    public static ProductRequest validRequest(){
        ProductRequest validProductRequest = ProductRequest.builder()
                .name("Smartphone")
                .description("Latest model with high-end features")
                .quantity(10)
                .amount(599.99)
                .categories(List.of(new CategoryDto(1, "Electronics")))
                .build();
        return validProductRequest;
    }
    public static ProductRequest invalidRequest(){
        ProductRequest invalidProductRequest = ProductRequest.builder()
                .name("")  // Invalid due to @NotBlank
                .description("")  // Invalid due to @NotBlank
                .quantity(0)  // Invalid due to @Min(1)
                .amount(0.0)  // Invalid due to @DecimalMin(inclusive = false)
                .categories(null)  // Invalid due to @NotNull
                .build();
        return invalidProductRequest;
    }
    public static ProductResponse responseForValidRequest(){
        ProductResponse productResponse = ProductResponse.builder()
                .id(1)  // Valid ID
                .name("Smartphone")  // Valid name
                .description("Latest model with high-end features")  // Valid description
                .quantity(10)  // Valid quantity
                .amount(599.99)  // Valid amount
                .categories(List.of(new CategoryDto(1, "Electronics")))  // Valid categories
                .img(null)  // Image is null
                .build();

        return productResponse;
    }
}
