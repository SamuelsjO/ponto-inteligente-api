package com.samuelTI.smartpoint.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SmartPointApplication{

	public static void main(String[] args) {
		SpringApplication.run(SmartPointApplication.class, args);
		
	}
}
