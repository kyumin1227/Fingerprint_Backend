package com.example.fingerprint_backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication(exclude = {
		CompositeMeterRegistryAutoConfiguration.class,
		DataSourcePoolMetricsAutoConfiguration.class,
		TomcatMetricsAutoConfiguration.class,
		SimpleMetricsExportAutoConfiguration.class,
		SystemMetricsAutoConfiguration.class
})
@EnableScheduling
public class FingerprintBackendApplication {

	public static void main(String[] args) {
//		Dotenv dotenv = Dotenv.load();
////		Dotenv dotenv = Dotenv.configure()
////						.directory("/home/ubuntu/spring/Fingerprint_Backend")
////								.load();
//
//		System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
//		System.setProperty("DB_USER", dotenv.get("DB_USER"));
//		System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
//		System.setProperty("DB_URL", dotenv.get("DB_URL"));
//		System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));
//		System.setProperty("JWT_EXPIRATION", dotenv.get("JWT_EXPIRATION"));
//		System.setProperty("LINE_ACCESS_TOKEN", dotenv.get("LINE_ACCESS_TOKEN"));

		SpringApplication.run(FingerprintBackendApplication.class, args);
	}

}
