package it.tinna.smartdoc.server.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.server.delegate.scadenzario.ScadenzarioPromemoriaDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

/**
 * Scheduler per l'invio automatico dei promemoria di scadenza (incassi e pagamenti).
 * Itera tutti i tenant attivi ed elabora le regole configurate per ciascuno.
 */
@Component
@ConditionalOnProperty(name = "smartdoc.batch.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class ScadenzarioPromemoriaScheduler
{

    private static final Logger                 log = LoggerFactory.getLogger(ScadenzarioPromemoriaScheduler.class);

    @Autowired
    private MunicipalityDelegate                municipalityDelegate;

    @Autowired
    private ScadenzarioPromemoriaDelegate        scadenzarioPromemoriaDelegate;

    /**
     * Esecuzione giornaliera, di default alle 07:00.
     */
    @Scheduled(cron = "${smartdoc.batch.scadenzario.cron:0 0 7 * * *}")
    public void runPromemoria()
    {
        DatabaseContextHolder.clear();
        log.info("Inizio esecuzione pianificata: Promemoria Scadenzario");
        try
        {
            List<MunicipalityDto> tenantAttivi = municipalityDelegate.getAziendeAttive();
            log.info("Trovati {} tenant attivi per l'elaborazione dei promemoria", tenantAttivi.size());

            for ( MunicipalityDto tenant : tenantAttivi )
            {
                String dbKey = tenant.getDbName();
                try
                {
                    DatabaseContextHolder.set(dbKey);
                    scadenzarioPromemoriaDelegate.processaPromemoria();
                    log.info("Promemoria elaborati per tenant: {}", dbKey);
                }
                catch ( Exception e )
                {
                    log.error("Errore nell'elaborazione dei promemoria per il tenant: {}", dbKey, e);
                }
                finally
                {
                    DatabaseContextHolder.clear();
                }
            }
        }
        catch ( Exception e )
        {
            log.error("Errore generico durante il recupero dei tenant o l'elaborazione dei promemoria", e);
        }
    }

}
