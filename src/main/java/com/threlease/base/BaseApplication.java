package com.threlease.base;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@EnableJpaRepositories
@SpringBootApplication
public class BaseApplication {
	public static void main(String[] args) {
		String currentDirectory = System.getProperty("user.dir");

		try {
			Path companyDirectory = Paths.get(currentDirectory + File.separator + "company/");
			if (!Files.exists(companyDirectory) && !Files.isDirectory(companyDirectory)) {
				Files.createDirectory(companyDirectory);
			}

			Path companyProfileDirectory = Paths.get(currentDirectory + File.separator + "company/profile/");
			if (!Files.exists(companyProfileDirectory) && !Files.isDirectory(companyProfileDirectory)) {
				Files.createDirectory(companyProfileDirectory);
			}

			Path restaurantDirectory = Paths.get(currentDirectory + File.separator + "restaurant/");
			if (!Files.exists(restaurantDirectory) && !Files.isDirectory(restaurantDirectory)) {
				Files.createDirectory(restaurantDirectory);
			}

			Path restaurantProfileDirectory = Paths.get(currentDirectory + File.separator + "restaurant/profile/");
			if (!Files.exists(restaurantProfileDirectory) && !Files.isDirectory(restaurantProfileDirectory)) {
				Files.createDirectory(restaurantProfileDirectory);
			}
		} catch (IOException e) {
			System.out.println("Required Directory");
			return;
		}

		SpringApplication.run(BaseApplication.class, args);
	}
}
