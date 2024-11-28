package com.hieptran.smarthome_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class SmarthomeServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmarthomeServerApplication.class, args);
	}

}
