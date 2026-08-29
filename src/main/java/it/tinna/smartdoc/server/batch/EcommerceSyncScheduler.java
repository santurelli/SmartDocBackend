package it.tinna.smartdoc.server.batch;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.tinna.smartdoc.server.dao.ecommerce.EcommerceConfigDao;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.ecommerce.EcommerceIntegrationDelegate;
import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.shared.dto.ecommerce.EcommerceConfigDto;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Polling periodico degli ordini WooCommerce per tutti i tenant che hanno l'integrazione abilitata.
 * Ogni tenant ha il proprio intervallo configurabile (default 15 minuti): il ciclo gira ogni 5 minuti
 * e per ciascun tenant abilitato verifica se e' trascorso abbastanza tempo dall'ultima sincronizzazione.
 */
@Component
@ConditionalOnProperty(name = "smartdoc.batch.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class EcommerceSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(EcommerceSyncScheduler.class);

    @Autowired
    private MunicipalityDelegate municipalityDelegate;

    @Autowired
    private EcommerceIntegrationDelegate ecommerceIntegrationDelegate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Scheduled(cron = "${smartdoc.batch.ecommerce.cron:0 */5 * * * *}")
    public void runSync() {
        DatabaseContextHolder.clear();
        log.info("Inizio ciclo di polling e-commerce");
        try {
            List<MunicipalityDto> tenantAttivi = municipalityDelegate.getAziendeAttive();
            for (MunicipalityDto tenant : tenantAttivi) {
                String dbKey = tenant.getDbName();
                try {
                    DatabaseContextHolder.set(dbKey);
                    if (isDovutoSync(dbKey)) {
                        ecommerceIntegrationDelegate.sincronizza();
                    }
                } catch (Exception e) {
                    log.error("Errore nel polling e-commerce per il tenant: {}", dbKey, e);
                } finally {
                    DatabaseContextHolder.clear();
                }
            }
        } catch (Exception e) {
            log.error("Errore generico durante il polling e-commerce", e);
        }
    }

    private boolean isDovutoSync(String dbKey) {
        EcommerceConfigDao dao = new EcommerceConfigDao(jdbcTemplate);
        EcommerceConfigDto config = dao.getForCurrentTenant();
        if (config == null || config.getFlAbilitato() == null || config.getFlAbilitato() != 1) {
            return false;
        }
        LocalDateTime ultimoSync = dao.getUltimoSync(config);
        if (ultimoSync == null) {
            return true;
        }
        int intervallo = config.getIntervalloMinuti() != null ? config.getIntervalloMinuti() : 15;
        return ChronoUnit.MINUTES.between(ultimoSync, LocalDateTime.now()) >= intervallo;
    }
}
