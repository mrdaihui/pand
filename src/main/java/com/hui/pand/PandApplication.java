package com.hui.pand;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.hui.pand.**"})
public class PandApplication {

	public static void main(String[] args) {
		SpringApplication.run(PandApplication.class, args);
	}

}
