package com.ayush.ayush.mapper;

import com.ayush.ayush.dto.SellerRegistrationRequest;
import com.ayush.ayush.model.Seller;

public class SellerRequestMapper {

    public static Seller mapToSeller(SellerRegistrationRequest request){
        Seller seller = new Seller();
        seller.setName(request.getName());
        seller.setUsername(request.getUsername());
        seller.setPassword(request.getPassword());
        seller.setAddress(request.getAddress());
        seller.setGstId(request.getGstId());
        seller.setCategories(CategoryMapper.toSellerCategoryEntity(request.getCategories()));

        return seller;

    }
}
