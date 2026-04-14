package it.tinna.smartdoc.server.batch;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;

/**
 * Scheduler nativo per l'avvio periodico dei job Spring Batch.
 * Recupera dinamicamente i tenant attivi dal database centrale.
 */
@Component
@ConditionalOnProperty(name = "smartdoc.batch.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class BatchScheduler {

    private static final Logger log = LoggerFactory.getLogger(BatchScheduler.class);

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("JOB_INVIO_FATTUREELETTRONICHE")
    private Job jobInvioFatture;

    @Autowired
    @Qualifier("JOB_RICEZIONE_ESITISDI")
    private Job jobRicezioneEsiti;

    @Autowired
    @Qualifier("JOB_RICEZIONE_ESITIINVIO")
    private Job jobRicezioneEsitiInvio;

    @Autowired
    private MunicipalityDelegate municipalityDelegate;

    /**
     * Job di invio fatture elettroniche.
     * Frequenza: Ogni 10 minuti (configurabile via properties).
     */
    @Scheduled(cron = "${smartdoc.batch.invio.cron:0 */10 * * * *}")
    public void runInvioFatture() {
        log.info("Inizio esecuzione pianificata: Invio Fatture Elettroniche");
        try {
            List<MunicipalityDto> activeTenants = municipalityDelegate.getAziendeConFatturazioneElettronica();
            log.info("Trovati {} tenant attivi per l'invio", activeTenants.size());

            for (MunicipalityDto tenant : activeTenants) {
                String dbKey = tenant.getDbName();
                log.info("Avvio Job invio per tenant: {}", dbKey);
                
                try {
                    JobParameters params = new JobParametersBuilder()
                            .addString(BatchConstants.JOBPARAM_DB_KEY, dbKey)
                            .addLong("timestamp", System.currentTimeMillis())
                            .toJobParameters();
                    
                    jobLauncher.run(jobInvioFatture, params);
                    log.info("Job completato con successo per tenant: {}", dbKey);
                } catch (Exception e) {
                    log.error("Errore durante l'esecuzione del job per il tenant: {}", dbKey, e);
                }
            }
        } catch (Exception e) {
            log.error("Errore generico durante il recupero dei tenant o l'avvio dello scheduler di invio", e);
        }
    }

    /**
     * Job di ricezione esiti SDI.
     * Frequenza: Ogni 30 minuti (configurabile via properties).
     */
    @Scheduled(cron = "${smartdoc.batch.esiti.cron:0 */30 * * * *}")
    public void runRicezioneEsiti() {
        log.info("Inizio esecuzione pianificata: Ricezione Esiti SDI");
        try {
            // Alcuni job di ricezione esiti agiscono globalmente o usano parametri specifici.
            // In base all'XML RicezioneEsitiSdi_JobDefinitions, il job gestisce i tracciamenti centrali.
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(jobRicezioneEsiti, params);
            log.info("Job Ricezione Esiti SDI completato.");
        } catch (Exception e) {
            log.error("Errore durante l'esecuzione del job Ricezione Esiti SDI", e);
        }
    }

    /**
     * Job di ricezione esiti invio (dal provider).
     * Frequenza: Ogni 2 ore (configurabile via properties).
     */
    @Scheduled(cron = "${smartdoc.batch.esiti_invio.cron:0 0 11,13,15,17,19,21,23 * * *}")
    public void runRicezioneEsitiInvio() {
        log.info("Inizio esecuzione pianificata: Ricezione Esiti Invio");
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(jobRicezioneEsitiInvio, params);
            log.info("Job Ricezione Esiti Invio completato.");
        } catch (Exception e) {
            log.error("Errore durante l'esecuzione del job Ricezione Esiti Invio", e);
        }
    }
}
