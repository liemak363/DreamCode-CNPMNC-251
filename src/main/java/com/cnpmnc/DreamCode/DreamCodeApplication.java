package com.cnpmnc.DreamCode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

// Disable Spring Security auto-configuration to turn off login/authentication entirely
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DreamCodeApplication {

	public static void main(String[] args) {
		SpringApplication.run(DreamCodeApplication.class, args);
	}

}
