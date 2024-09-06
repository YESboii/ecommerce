package com.ayush.ayush.dto;

import com.ayush.ayush.model.SellerCategory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public final class SellerRegistrationRequest extends RegistrationRequest {
    private String gstId;
    private List<SellerCategory> categories;
}
