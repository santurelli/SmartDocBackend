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
public class RicezioneEsitiSdiJob implements Job
{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException
    {
        ClassPathXmlApplicationContext springContext = new ClassPathXmlApplicationContext("/config/main-config.xml", "classpath:jobs/RicezioneEsitiSdi_JobDefinitions.xml");
        logger.info("Avvio job ricezione esiti sdi direttamente tramite JobLauncher");
        try
        {
            org.springframework.batch.core.launch.JobLauncher jobLauncher = (org.springframework.batch.core.launch.JobLauncher) springContext.getBean(org.springframework.batch.core.launch.JobLauncher.class);
            org.springframework.batch.core.Job job = (org.springframework.batch.core.Job) springContext.getBean("JOB_RICEZIONE_ESITISDI");

            org.springframework.batch.core.JobParameters params = new org.springframework.batch.core.JobParametersBuilder()
                    .addString("dtExecution", org.apache.commons.lang3.time.DateFormatUtils.format(new java.util.Date(), "yyyyMMddHHmmss"))
                    .toJobParameters();
            
            try
            {
                org.springframework.batch.core.JobExecution execution = jobLauncher.run(job, params);
                logger.info("Job di ricezione esiti sdi terminato con stato: {}", execution.getStatus());
            }
            catch ( Exception e )
            {
                logger.error("Errore nel lancio del batch di ricezione esiti sdi", e);
            }
        }
        finally
        {
            springContext.close();
        }
    }

}

