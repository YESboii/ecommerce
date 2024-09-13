package com.ayush.ayush.controllers.seller;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithSellerSecurityContextFactory.class)
public @interface WithSeller {
    String id() default "1";
    String username() default "xyz@gmail.com";
    String password() default "12345";
}
