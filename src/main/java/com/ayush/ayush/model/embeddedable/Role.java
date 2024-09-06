package com.ayush.ayush.model.embeddedable;

import org.springframework.security.core.GrantedAuthority;

//Assumption, Each Have One Role
public enum Role implements GrantedAuthority {
    CUSTOMER,SELLER;

    @Override
    public String getAuthority() {
        return this.name();
    }
}
