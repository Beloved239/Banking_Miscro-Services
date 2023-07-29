package com.beloved.identityservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;


@SpringBootApplication
@EnableDiscoveryClient
public class IdentityServicesApplication {
//	@Bean
//	public WebMvcConfigurer webMvcConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//
//				registry
//						.addMapping("/**")
//						.allowedOrigins("*", "http://127.0.0.1", "http://example.org", "http://localhost", "http://localhost:5500")
//						.allowedHeaders("*")
//						.allowedMethods("POST", "GET", "PUT", "PATCH", "OPTIONS");
//			}
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(IdentityServicesApplication.class, args);
	}

}
