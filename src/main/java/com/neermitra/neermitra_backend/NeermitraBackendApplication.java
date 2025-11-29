package com.neermitra.neermitra_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NeermitraBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeermitraBackendApplication.class, args);
		
		System.out.println("Neermitra !");
	}

}


/*
 * AuthController → AuthenticationManager (Spring Security) →
 * CustomUserDetailsService → JWT ↓ (just reads user for response)
 * CustomerSignupService.findByPhone() ← helper method only
 */
