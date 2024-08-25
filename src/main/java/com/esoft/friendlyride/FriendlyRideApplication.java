package com.esoft.friendlyride;

import org.n52.jackson.datatype.jts.JtsModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FriendlyRideApplication {

	public static void main(String[] args) {
		SpringApplication.run(FriendlyRideApplication.class, args);
	}


	@Bean
	public JtsModule jtsModule() {
		return new JtsModule();
	}
}
