package com.blog.writeapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WriteapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WriteapiApplication.class, args);
	}

}
