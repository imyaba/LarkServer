package org.github.hollykunge.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@EnableDiscoveryClient
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
	
//	@Bean
//	public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
//	PropertySourcesPlaceholderConfigurer c = new PropertySourcesPlaceholderConfigurer();
//	c.setIgnoreUnresolvablePlaceholders(true);
//	return c;
//	}
//	
//	@Bean
//	public static PropertySourceLoader getPropertiesPropertySourceLoader () {
//		NewPropertySourcesPlaceholderConfigurer nspc= 
//				new NewPropertySourcesPlaceholderConfigurer();
//		return nspc;
//	}
}
