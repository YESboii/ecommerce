package com.ayush.ayush.controllers.seller;

import com.ayush.ayush.model.Seller;
import com.ayush.ayush.model.embeddedable.Role;
import com.ayush.ayush.security.SellerAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithSellerSecurityContextFactory implements WithSecurityContextFactory<WithSeller> {


    @Override
    public SecurityContext createSecurityContext(WithSeller annotation) {
        Seller seller = new Seller();
        seller.setRole(Role.SELLER);
        seller.setEnabled(true);
        seller.setTwoFactorEnabled(false);
        seller.setName("Ayush");
        Long id = Long.parseLong(annotation.id());
        String username = annotation.username();
        String password = annotation.password();

        seller.setId(id);
        seller.setPassword(password);
        seller.setUsername(username);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication token = new SellerAuthenticationToken(seller,password,seller.getAuthorities());
        context.setAuthentication(token);
        return context;
    }
}
