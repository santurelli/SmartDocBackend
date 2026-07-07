package it.tinna.smartdoc.server.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import it.tinna.smartdoc.server.database.SmartDocRoutingDataSource;
import it.tinna.smartdoc.server.database.TenantAwareDataSource;

@Configuration
public class DataSourceConfig {

    // Common Credentials (service DB - superuser)
    @Value("${datasource.common.username}")
    private String commonUsername;
    @Value("${datasource.common.password}")
    private String commonPassword;
    @Value("${datasource.common.driver-class-name}")
    private String commonDriverClassName;

    // Shared DB Credentials (utente normale, soggetto a RLS)
    @Value("${datasource.shared.username:${datasource.common.username}}")
    private String sharedDbUsername;
    @Value("${datasource.shared.password:${datasource.common.password}}")
    private String sharedDbPassword;

    // Service DB
    @Value("${spring.datasource.url}")
    private String serviceDbUrl;

    // Shared DB for Tenants
    @Value("${datasource.shared.url}")
    private String sharedDbUrl;

    public DataSource createDataSource(String url) {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(commonDriverClassName)
                .url(url)
                .username(sharedDbUsername)
                .password(sharedDbPassword)
                .build();
        
        dataSource.setMaximumPoolSize(20);      // Pool unico condiviso
        dataSource.setMinimumIdle(2);
        dataSource.setIdleTimeout(60000);
        dataSource.setPoolName("HikariPool-Shared");
        
        return dataSource;
    }

    @Bean(name = "servicedbDataSource")
    public DataSource servicedbDataSource() {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(commonDriverClassName)
                .url(serviceDbUrl)
                .username(commonUsername)
                .password(commonPassword)
                .build();
        dataSource.setMaximumPoolSize(5);
        dataSource.setMinimumIdle(1);
        dataSource.setIdleTimeout(60000);
        dataSource.setPoolName("HikariPool-Service");
        return dataSource;
    }

    @Bean(name = "shareddbDataSource")
    public DataSource shareddbDataSource() {
        return createDataSource(sharedDbUrl);
    }

    @Bean(name = "tenantAwareDataSource")
    public DataSource tenantAwareDataSource() {
        return new TenantAwareDataSource(shareddbDataSource(), servicedbDataSource());
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        SmartDocRoutingDataSource routingDataSource = new SmartDocRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        targetDataSources.put("servicedb", servicedbDataSource());
        targetDataSources.put("shareddb", tenantAwareDataSource());

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(servicedbDataSource());
        return routingDataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
    
    @Bean(name = "serviceJdbcTemplate")
    public JdbcTemplate serviceJdbcTemplate(@Qualifier("servicedbDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean(name = "serviceTransactionManager")
    public PlatformTransactionManager serviceTransactionManager(@Qualifier("servicedbDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
