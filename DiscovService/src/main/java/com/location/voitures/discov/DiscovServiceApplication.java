package com.location.voitures.discov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
@EnableEurekaServer
@SpringBootApplication
public class DiscovServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscovServiceApplication.class, args);
	}

}
