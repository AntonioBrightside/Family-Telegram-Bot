package com.amam.familybot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class FamilyBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(FamilyBotApplication.class, args);
	}

}
