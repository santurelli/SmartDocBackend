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
    private FreeMarkerConfigurer       freeMarkerConfiguration;

    @Setter
    private MailSenderService          mailSenderService;

    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception
    {
        try
        {
            // List<NotificaFatturaDto> list = fatturaelettronicaDelegate.getNotificheFastOrder();
            List<NotificaFatturaDto> list = scartiExtractor.getNotifiche();
            if ( list != null && !list.isEmpty() )
            {
                Map<String, Object> model = new HashMap<>();
                model.put("notifiche", list);
                model.put("currentYear", DateFormatUtils.format(new Date(), "YYYY"));
                try
                {
                    Template template1 = freeMarkerConfiguration.getConfiguration().getTemplate("notifica_fatture.ftl");
                    Writer out = new StringWriter();
                    template1.process(model, out);
                    Map<String, Resource> imgs = new HashMap<>();
                    imgs.put("headerimg", new ClassPathResource("email-header.png"));

                    List<Object[]> batchArgs = new ArrayList<Object[]>();
                    for ( NotificaFatturaDto dto : list )
                    {
                        Object[] params = new Object[1];
                        params[0] = dto.getId();
                        batchArgs.add(params);
                    }
                    try
                    {
                        fatturaelettronicaDelegate.impostaFattureNotificate(batchArgs);
                        File f = File.createTempFile("eml", "eml");
                        FileUtils.writeStringToFile(f, out.toString(), StandardCharsets.UTF_8);
                        try
                        {
                            mailSenderService.send("Report anomalie fatture elettroniche", out.toString(), null, null);
                        }
                        catch ( MailException e )
                        {
                            logger.error("Errore nell'invio della mail con le anomalie delle fatture per FastOrder", e);
                            try
                            {
                                fatturaelettronicaDelegate.impostaFattureNonNotificate(batchArgs);
                            }
                            catch ( SQLException e1 )
                            {

                            }
                        }
                    }
                    catch ( SQLException e )
                    {

                    }
                }
                catch ( IOException e )
                {
                    logger.error("Errore nella generazione del testo della mail con le fatture con esiti di scarto o xml non valido per FastOrder", e);
                }
            }
        }
        catch ( SQLException e )
        {
            throw new Exception(ExceptionUtils.getMessage(e));
        }
        return RepeatStatus.FINISHED;
    }

}

