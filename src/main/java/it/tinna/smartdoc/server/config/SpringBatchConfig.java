package it.tinna.smartdoc.server.config;

import javax.sql.DataSource;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Configurazione esplicita per Spring Batch 5.
 * Forza l'utilizzo del database di servizio e del relativo transaction manager 
 * per tutti i metadati del job repository, evitando il routing sui tenant.
 */
@Configuration
public class SpringBatchConfig extends DefaultBatchConfiguration {

    @Autowired
    @Qualifier("servicedbDataSource")
    private DataSource servicedbDataSource;

    @Autowired
    @Qualifier("serviceTransactionManager")
    private PlatformTransactionManager serviceTransactionManager;

    @Override
    protected DataSource getDataSource() {
        return servicedbDataSource;
    }

    @Override
    protected PlatformTransactionManager getTransactionManager() {
        return serviceTransactionManager;
    }
}
