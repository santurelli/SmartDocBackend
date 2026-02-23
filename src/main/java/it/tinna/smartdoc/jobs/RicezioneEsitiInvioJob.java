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

@DisallowConcurrentExecution
public class RicezioneEsitiInvioJob implements Job
{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("/config/main-config.xml");
        logger.info("Avvio job ricezione esito invio fatture elettroniche");
        RestTemplate restTemplate = new RestTemplate();
        ConfigurazioneDelegate configurazioneDelegate = (ConfigurazioneDelegate) springContext.getBean("configurazioneDelegate");
        try
        {
            String urlAvvioBatch = configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_JOB_LETTURA_ESITI_INVIO, BatchConstants.CONFIG_KEY_URL_AVVIO_BATCH);
            try
            {
                ResponseEntity<String> response = restTemplate.getForEntity(urlAvvioBatch, String.class);
                logger.info("Risposta alla chiamata del job di lettura esiti invio: {}", response.getBody());
            }
            catch ( RestClientException e )
            {
                logger.error("Errore nella chiamata all'url per l'avvio del batch di lettura esiti invio fatture", e);
            }
        }
        catch ( SQLException e )
        {
            logger.error("Errore nel recupero dell'url per l'avvio del batch di lettura degli esiti invio fatture", e);
        }
        finally
        {
            springContext.close();
        }

//        JobParametersBuilder jpb = new JobParametersBuilder().addString("dtExecution", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
//        String params = jpb.toJobParameters().toString();
//        params = params.substring(1);
//        params = params.substring(0, params.length() - 1);
//        try
//        {
//            JobOperator jobOperator = (JobOperator) springContext.getBean("jobOperator");
//            jobOperator.start("JOB_RICEZIONE_ESITIINVIO", params);
//        }
//        catch ( NoSuchJobException | JobInstanceAlreadyExistsException | JobParametersInvalidException e )
//        {
//            logger.error("Errore nell'avvio del batch di ricezione degli esiti di invio delle fatture elettroniche", e);
//        }
//        finally
//        {
//            springContext.close();
//        }
    }

}

