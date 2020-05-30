package com.alchemistscode.integrator;

import com.alchemistscode.integrator.config.RoutesProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RoutesProperties.class)
@EntityScan("com.alchemistscode.sepomex.commons.entity")
public class SpringBootWebApplication {
    public static void main(final String[] args){
        SpringApplication.run(SpringBootWebApplication.class, args);
    }
}
