package it.tinna.smartdoc.jobs;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@DisallowConcurrentExecution
public class InvioFattureElettronicheJob implements Job
{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    private MunicipalityDelegate municipalityDelegate;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("/config/main-config.xml");
        try
        {
            MunicipalityDelegate municipalityDelegate = (MunicipalityDelegate) springContext.getBean("municipalityDelegate");
//            springContext.getAutowireCapableBeanFactory().autowireBean(municipalityDelegate);
            ConfigurazioneDelegate configurazioneDelegate = (ConfigurazioneDelegate) springContext.getBean("configurazioneDelegate");
            String urlAvvioBatch = configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_JOB_INVIO_FATTURE, BatchConstants.CONFIG_KEY_URL_AVVIO_BATCH);
            List<MunicipalityDto> list = municipalityDelegate.getAziendeConFatturazioneElettronica();
            if ( list != null )
            {
                RestTemplate restTemplate = new RestTemplate();
//                JobOperator jobOperator = (JobOperator) springContext.getBean("jobOperator");
//              jobOperator.start("JOB_RICEZIONE_ESITISDI", params);
                for ( MunicipalityDto municipalityDto : list )
                {
                    logger.info("Trovata azienda {} con servizio di fatturazione elettronica attivo. Cerco di avviare il batch di invio fatture elettroniche", municipalityDto.getLabel());
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("DB_KEY", municipalityDto.getDbName());
                    try
                    {
                        ResponseEntity<String> response = restTemplate.getForEntity(urlAvvioBatch, String.class, map);
                        logger.info("Risposta alla chiamata del job di invio fatture elettroniche l'azienda {}: {}", municipalityDto.getLabel(), response.getBody());
                    }
                    catch ( RestClientException e )
                    {
                        logger.error("Errore nella chiamata all'url per l'avvio del batch di invio fatture per l'azienda {}", municipalityDto.getLabel(), e);
                    }
//                    JobParametersBuilder jpb = new JobParametersBuilder().addString("dtExecution", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss")).addString(BatchConstants.JOBPARAM_DB_KEY, municipalityDto.getDbName());
//                    String params = jpb.toJobParameters().toString();
//                    params = params.substring(1);
//                    params = params.substring(0, params.length() - 1);
//                    try
//                    {
//                        jobOperator.start("JOB_INVIO_FATTUREELETTRONICHE", params);
//                    }
//                    catch ( NoSuchJobException | JobInstanceAlreadyExistsException | JobParametersInvalidException e )
//                    {
//                        logger.error("Errore nell'avvio del batch di invio fatture elettroniche per l'azienda {}", municipalityDto.getLabel(), e);
//                    }
                }
            }
        }
        catch ( SQLException e )
        {
        }
        finally
        {
            springContext.close();
        }
    }

}

