package it.tinna.smartdoc.jobs;

import java.sql.SQLException;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;
import java.util.List;

@DisallowConcurrentExecution
public class RicezioneEsitiInvioJob implements Job
{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("/config/main-config.xml", "classpath:jobs/RicezioneEsitiInvio_JobDefinitions.xml");
        logger.info("Avvio job ricezione esito invio fatture elettroniche direttamente tramite JobLauncher");
        try
        {
            MunicipalityDelegate municipalityDelegate = (MunicipalityDelegate) springContext.getBean("municipalityDelegate");
            org.springframework.batch.core.launch.JobLauncher jobLauncher = (org.springframework.batch.core.launch.JobLauncher) springContext.getBean(org.springframework.batch.core.launch.JobLauncher.class);
            org.springframework.batch.core.Job job = (org.springframework.batch.core.Job) springContext.getBean("JOB_RICEZIONE_ESITIINVIO");

            List<MunicipalityDto> list = municipalityDelegate.getAziendeConFatturazioneElettronica();
            if ( list != null )
            {
                for ( MunicipalityDto municipalityDto : list )
                {
                    logger.info("Trovata azienda {} con servizio di fatturazione elettronica attivo. Avvio il batch di lettura esiti invio direttamente.", municipalityDto.getLabel());
                    
                    org.springframework.batch.core.JobParameters params = new org.springframework.batch.core.JobParametersBuilder()
                            .addString(BatchConstants.JOBPARAM_DB_KEY, municipalityDto.getDbName())
                            .addString("dtExecution", org.apache.commons.lang3.time.DateFormatUtils.format(new java.util.Date(), "yyyyMMddHHmmss"))
                            .toJobParameters();
                    
                    try
                    {
                        org.springframework.batch.core.JobExecution execution = jobLauncher.run(job, params);
                        logger.info("Job di lettura esiti invio per l'azienda {} terminato con stato: {}", municipalityDto.getLabel(), execution.getStatus());
                    }
                    catch ( Exception e )
                    {
                        logger.error("Errore nel lancio del batch di lettura esiti invio per l'azienda {}", municipalityDto.getLabel(), e);
                    }
                }
            }
        }
        catch ( SQLException e )
        {
            logger.error("Errore nel recupero delle aziende dalla base dati", e);
        }
        finally
        {
            springContext.close();
        }
    }

}

