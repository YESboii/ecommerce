package com.ayush.ayush;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class AyushApplication {

	@GetMapping
	public String test(){
		return "hello from container";
	}

	public static void main(String[] args) {
		SpringApplication.run(AyushApplication.class, args);
	}

}
