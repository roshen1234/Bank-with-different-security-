package com.eazybytes.springsecsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Springsecsection1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springsecsection1Application.class, args);
        System.out.println("hi");

	}

}
