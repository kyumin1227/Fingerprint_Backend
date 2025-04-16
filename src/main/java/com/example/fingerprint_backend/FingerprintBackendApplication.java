package com.example.fingerprint_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.SystemMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.jdbc.DataSourcePoolMetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

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

        SpringApplication.run(FingerprintBackendApplication.class, args);
    }

}
