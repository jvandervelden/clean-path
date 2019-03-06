package org.vandervj.cleanPath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"org.vandervj"})
public class CleanFloorApplication {
	public static void main(String[] args) {
		SpringApplication.run(CleanFloorApplication.class, args);
	}
}
