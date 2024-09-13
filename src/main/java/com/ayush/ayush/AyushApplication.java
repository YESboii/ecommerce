package com.ayush.ayush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@EnableJpaAuditing
@SpringBootApplication
public class AyushApplication {

//	AccessDeniedHandler

	@GetMapping
	public String test(){
		System.out.println("hello");
		return "hello from container";
	}

	public static void main(String[] args) {
		SpringApplication.run(AyushApplication.class, args);
	}

}
