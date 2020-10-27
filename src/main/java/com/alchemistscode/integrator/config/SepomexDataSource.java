package com.alchemistscode.integrator.config;

import com.alchemistscode.sepomex.entity.Municipality;
import com.alchemistscode.sepomex.entity.Settlement;
import com.alchemistscode.sepomex.entity.catalog.SettlementType;
import com.alchemistscode.sepomex.entity.catalog.State;
import com.alchemistscode.sepomex.entity.catalog.Zone;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(basePackages = "com.alchemistscode.integrator.repository",
        entityManagerFactoryRef = "sepomexEntityManagerFactory",
        transactionManagerRef= "sepomexTransactionManager")
public class SepomexDataSource {
    @Bean
    @ConfigurationProperties("spring.datasource.sepomex")
    public DataSourceProperties memberDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "sepomexDS")
    @ConfigurationProperties("spring.datasource.sepomex.configuration")
    public DataSource sepomexDataSource() {
        return memberDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(name = "sepomexEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean sepomexEntityManagerFactory(
            EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(sepomexDataSource())
                .packages(State.class, Municipality.class, Settlement.class, SettlementType.class, Zone.class)
                .build();
    }

    @Bean
    public PlatformTransactionManager sepomexTransactionManager(
            final @Qualifier("sepomexEntityManagerFactory") LocalContainerEntityManagerFactoryBean cardEntityManagerFactory) {
        return new JpaTransactionManager(cardEntityManagerFactory.getObject());
    }

}
