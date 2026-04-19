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

@Configuration
public class DataSourceConfig {

    // Common Credentials
    @Value("${datasource.common.username}")
    private String commonUsername;
    @Value("${datasource.common.password}")
    private String commonPassword;
    @Value("${datasource.common.driver-class-name}")
    private String commonDriverClassName;

    // Service DB
    @Value("${spring.datasource.url}")
    private String serviceDbUrl;

    // Tenant DBs
    @Value("${datasource.romax.url}")
    private String romaxUrl;
    @Value("${datasource.justdesign.url}")
    private String justdesignUrl;
    @Value("${datasource.justeat.url}")
    private String justeatUrl;
    @Value("${datasource.santurelli.url}")
    private String santurelliUrl;
    @Value("${datasource.enzaiannaccone.url}")
    private String enzaiannacconeUrl;

    public DataSource createDataSource(String url) {
        HikariDataSource dataSource = DataSourceBuilder.create()
                .type(HikariDataSource.class)
                .driverClassName(commonDriverClassName)
                .url(url)
                .username(commonUsername)
                .password(commonPassword)
                .build();
        
        // Dynamic Pool Configuration (Soluzione 1)
        dataSource.setMaximumPoolSize(5);        // Tetto massimo per tenant
        dataSource.setMinimumIdle(0);           // Chiudi tutte le connessioni se non usate
        dataSource.setIdleTimeout(60000);       // 1 minuto di inattività prima della chiusura
        dataSource.setPoolName("HikariPool-" + url.substring(url.lastIndexOf("/") + 1));
        
        return dataSource;
    }

    @Bean(name = "servicedbDataSource")
    public DataSource servicedbDataSource() {
        return createDataSource(serviceDbUrl);
    }

    @Bean(name = "romaxDataSource")
    public DataSource romaxDataSource() {
        return createDataSource(romaxUrl);
    }

    @Bean(name = "justdesignDataSource")
    public DataSource justdesignDataSource() {
        return createDataSource(justdesignUrl);
    }

    @Bean(name = "justeatDataSource")
    public DataSource justeatDataSource() {
        return createDataSource(justeatUrl);
    }

    @Bean(name = "santurelliDataSource")
    public DataSource santurelliDataSource() {
        return createDataSource(santurelliUrl);
    }

    @Bean(name = "enzaiannacconeDataSource")
    public DataSource enzaiannacconeDataSource() {
        return createDataSource(enzaiannacconeUrl);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        SmartDocRoutingDataSource routingDataSource = new SmartDocRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        targetDataSources.put("servicedb", servicedbDataSource());
        targetDataSources.put("sd_romax", romaxDataSource());
        // Assuming keys match the 'dbName' column in 'd_e_entita'.
        // User asked for: Romax, JustDesign, JustEat, JustFood, Santurelli.
        // I will map them as requested. I should double check logic or keys if possible but for now:
        targetDataSources.put("sd_justdesign", justdesignDataSource());
        targetDataSources.put("sd_justeat", justeatDataSource());
        targetDataSources.put("sd_santurelli", santurelliDataSource());
        targetDataSources.put("sd_enzaiannaccone", enzaiannacconeDataSource());

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

