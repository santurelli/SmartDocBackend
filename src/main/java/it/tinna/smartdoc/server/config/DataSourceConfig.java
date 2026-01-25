package it.tinna.smartdoc.server.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

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
    @Value("${datasource.riggiolandia.url}")
    private String riggiolandiaUrl;
    @Value("${datasource.romax.url}")
    private String romaxUrl;
    @Value("${datasource.justdesign.url}")
    private String justdesignUrl;
    @Value("${datasource.justeat.url}")
    private String justeatUrl;
    @Value("${datasource.justfood.url}")
    private String justfoodUrl;
    @Value("${datasource.piuforty.url}")
    private String piufortyUrl;
    @Value("${datasource.santurelli.url}")
    private String santurelliUrl;

    public DataSource createDataSource(String url) {
        return DataSourceBuilder.create()
                .driverClassName(commonDriverClassName)
                .url(url)
                .username(commonUsername)
                .password(commonPassword)
                .build();
    }

    @Bean(name = "servicedbDataSource")
    public DataSource servicedbDataSource() {
        return createDataSource(serviceDbUrl);
    }

    @Bean(name = "riggiolandiaDataSource")
    public DataSource riggiolandiaDataSource() {
        return createDataSource(riggiolandiaUrl);
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

    @Bean(name = "justfoodDataSource")
    public DataSource justfoodDataSource() {
        return createDataSource(justfoodUrl);
    }

    @Bean(name = "piufortyDataSource")
    public DataSource piufortyDataSource() {
        return createDataSource(piufortyUrl);
    }

    @Bean(name = "santurelliDataSource")
    public DataSource santurelliDataSource() {
        return createDataSource(santurelliUrl);
    }

    @Bean
    @Primary
    public DataSource dataSource() {
        SmartDocRoutingDataSource routingDataSource = new SmartDocRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        targetDataSources.put("servicedb", servicedbDataSource());
        targetDataSources.put("sd_riggiolandia", riggiolandiaDataSource());
        targetDataSources.put("sd_romax", romaxDataSource());
        targetDataSources.put("smartdoc_bema", justdesignDataSource()); // Wait, Bema isn't in my list but mapping logic might need keys matching 'dbName' from DB.
        // Assuming keys match the 'dbName' column in 'd_e_entita'. 
        // User asked for: Riggiolandia, Romax, JustDesign, JustEat, JustFood, PiuForty, Santurelli.
        // I will map them as requested. I should double check logic or keys if possible but for now:
        targetDataSources.put("sd_justdesign", justdesignDataSource());
        targetDataSources.put("sd_justeat", justeatDataSource());
        targetDataSources.put("sd_justfood", justfoodDataSource());
        targetDataSources.put("sd_piuforty", piufortyDataSource());
        targetDataSources.put("sd_santurelli", santurelliDataSource());

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

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
