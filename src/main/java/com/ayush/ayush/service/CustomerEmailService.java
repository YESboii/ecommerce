package com.ayush.ayush.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("customerEmailService")
public class CustomerEmailService extends AbstractEmailService{

    private final String basePath;

    public CustomerEmailService(@Value("${app.registration.customer.base.uri}") String basePath) {
        this.basePath = basePath;
    }

    @Override
    String getBasePath() {
        return this.basePath;
    }
}
