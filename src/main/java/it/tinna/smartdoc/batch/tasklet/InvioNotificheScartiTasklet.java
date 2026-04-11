package it.tinna.smartdoc.batch.tasklet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import it.tinna.smartdoc.batch.dto.NotificaFatturaDto;
import it.tinna.smartdoc.batch.service.scarti.ScartiExtractor;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.service.mail.MailSenderService;
import lombok.Setter;

public class InvioNotificheScartiTasklet implements Tasklet
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Setter
    private ScartiExtractor            scartiExtractor;

    @Setter
    private Configuration              freeMarkerConfiguration;

    @Setter
    private MailSenderService          mailSenderService;

    @org.springframework.beans.factory.annotation.Value("${smartdoc.mail.subject.scarti}")
    private String                     mailSubject;

    @Autowired
    private it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate municipalityDelegate;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception
    {
        try
        {
            List<NotificaFatturaDto> list = scartiExtractor.getNotifiche();
            if ( list != null && !list.isEmpty() )
            {
                // Raggruppo le notifiche per dbKey (Ente/Negozio)
                Map<String, List<NotificaFatturaDto>> groupedNotifiche = new HashMap<>();
                for ( NotificaFatturaDto dto : list )
                {
                    groupedNotifiche.computeIfAbsent(dto.getDbKey(), k -> new ArrayList<>()).add(dto);
                }

                for ( Map.Entry<String, List<NotificaFatturaDto>> entry : groupedNotifiche.entrySet() )
                {
                    String dbKey = entry.getKey();
                    List<NotificaFatturaDto> groupList = entry.getValue();

                    // Recupero gli indirizzi email dal database (smartdoc_service_db.d_e_enti)
                    String destinationEmail = municipalityDelegate.getEmailErroriSdi(dbKey);
                    String[] recipients;

                    List<Object[]> batchArgs = new ArrayList<Object[]>();
                    for ( NotificaFatturaDto dto : groupList )
                    {
                        Object[] params = new Object[1];
                        params[0] = dto.getId();
                        batchArgs.add(params);
                    }

                    if ( org.apache.commons.lang3.StringUtils.isBlank(destinationEmail) )
                    {
                        logger.warn("Nessuna email_'errori_sdi' trovata per {}. Le notifiche verranno marcate come lette senza inviare la mail.", dbKey);
                        try
                        {
                            fatturaelettronicaDelegate.impostaFattureNotificate(batchArgs);
                        }
                        catch ( SQLException e )
                        {
                            logger.error("Errore database durante l'aggiornamento stato notifiche per {}", dbKey, e);
                        }
                        continue;
                    }

                    // Splitto per punto e virgola per gestire destinatari multipli
                    recipients = destinationEmail.split(";");

                    Map<String, Object> model = new HashMap<>();
                    model.put("notifiche", groupList);
                    model.put("currentYear", DateFormatUtils.format(new Date(), "YYYY"));
                    
                    try
                    {
                        java.io.InputStream is = getClass().getResourceAsStream("/template/notifica_fatture.ftl");
                        if (is == null) {
                            throw new IOException("Template /template/notifica_fatture.ftl non trovato nel classpath!");
                        }
                        Template template = new Template("notifica_fatture.ftl", 
                            new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8), 
                            freeMarkerConfiguration);
                        Writer out = new StringWriter();
                        template.process(model, out);


                        try
                        {
                            // Imposto come notificate prima dell'invio (strategia ottimistica del codice originale)
                            fatturaelettronicaDelegate.impostaFattureNotificate(batchArgs);
                            try
                            {
                                // Invio la mail al set specifico di destinatari usando l'oggetto dalle properties
                                String subject = (org.apache.commons.lang3.StringUtils.defaultIfBlank(mailSubject, "Report anomalie fatture elettroniche")) + " - " + dbKey;
                                mailSenderService.send(subject, out.toString(), null, recipients);
                                logger.info("Inviata mail report anomalie per {} a {}.", dbKey, java.util.Arrays.toString(recipients));
                            }
                            catch ( MailException e )
                            {
                                logger.error("Errore nell'invio della mail con le anomalie per {}", dbKey, e);
                                // Se fallisce l'invio, ripristino lo stato "non notificato"
                                fatturaelettronicaDelegate.impostaFattureNonNotificate(batchArgs);
                            }
                        }
                        catch ( SQLException e )
                        {
                            logger.error("Errore database durante l'aggiornamento stato notifiche per {}", dbKey, e);
                        }
                    }
                    catch ( IOException | freemarker.template.TemplateException e )
                    {
                        logger.error("Errore nella generazione del report per {}", dbKey, e);
                    }
                }
            }
        }
        catch ( Exception e )
        {
            logger.error("Errore generico nel tasklet invio notifiche", e);
            throw new Exception(ExceptionUtils.getMessage(e));
        }
        return RepeatStatus.FINISHED;
    }

}

