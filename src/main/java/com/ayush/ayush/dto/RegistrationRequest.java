package com.ayush.ayush.dto;

import com.ayush.ayush.model.embeddedable.Address;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class RegistrationRequest {
        String name;
        String username;
        String password;
        Address address;


}
