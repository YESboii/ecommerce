package com.ayush.ayush.mapper;

import com.ayush.ayush.dto.CustomerRegistrationRequest;
import com.ayush.ayush.model.Customer;

public class CustomerMapper {

    public static Customer toEntity(CustomerRegistrationRequest request){
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setUsername(request.getUsername());
        customer.setPassword(request.getPassword());
        customer.setAddress(request.getAddress());
        return customer;
    }
}
