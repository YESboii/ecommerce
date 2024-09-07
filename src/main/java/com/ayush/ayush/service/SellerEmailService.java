package com.ayush.ayush.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SellerEmailService extends AbstractEmailService{

    private final String basePath;

    public SellerEmailService(@Value("${app.registration.seller.base.uri}")String basePath){
        this.basePath = basePath;
    }
    @Override
    String getBasePath() {
        return basePath;
    }
}
