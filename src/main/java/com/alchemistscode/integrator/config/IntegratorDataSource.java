package com.alchemistscode.integrator.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class IntegratorDataSource {
    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.integrator")
    public DataSourceProperties integratorDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "integratorDS")
    @Primary
    @ConfigurationProperties("spring.datasource.integrator.configuration")
    public DataSource integratorDataSource() {
        return integratorDataSourceProperties().initializeDataSourceBuilder().build();
    }
}
