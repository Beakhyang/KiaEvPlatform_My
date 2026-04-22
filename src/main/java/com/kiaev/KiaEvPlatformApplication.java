package com.kiaev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.kiaev.common.aws.AwsSecretsBootstrap;

@SpringBootApplication
public class KiaEvPlatformApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(KiaEvPlatformApplication.class);
		application.setDefaultProperties(AwsSecretsBootstrap.loadDefaultProperties());
		application.run(args);
	}

}
