package com.ayush.ayush.model.embeddedable;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Address {

    private String streetName;
    private String city;
    private String zipCode;
    private String state;
    private String country;
}
