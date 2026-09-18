package com.eazybytes.springsecsection1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
//this below line will help us to show all filter related to security that got added and also other changes, to see in log we also need to add the application properties logging.level.org.springframework.security=DEBUG
//@EnableWebSecurity(debug = true)
//This is to enable method level security
@EnableMethodSecurity(jsr250Enabled = true,securedEnabled = true)
public class Springsecsection1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springsecsection1Application.class, args);
        System.out.println("hi");

	}

}
