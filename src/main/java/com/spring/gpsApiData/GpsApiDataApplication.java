package com.spring.gpsApiData;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GpsApiDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpsApiDataApplication.class, args);
	}

}
