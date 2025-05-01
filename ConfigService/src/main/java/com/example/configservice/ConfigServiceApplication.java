package com.example.configservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

import java.io.IOException;

@EnableConfigServer
@SpringBootApplication
public class ConfigServiceApplication {

	public static void main(String[] args) {
		try {
			ProcessBuilder builder = new ProcessBuilder(
				"cmd.exe", "/c",
				"cd C:\\Users\\Admin\\Documents\\workspace-spring-tool-suite-4-4.27.0.RELEASE\\ConfigService\\src\\main\\resources\\MyConfig && " +
				"git add . && " +
				"git commit -m \"Auto commit from ConfigService\" && " +
				"git push origin master"
			);
			builder.inheritIO(); 
			builder.start();
		} catch (IOException e) {
			System.err.println("Git auto-push failed: " + e.getMessage());
		}

		SpringApplication.run(ConfigServiceApplication.class, args);
	}
}
