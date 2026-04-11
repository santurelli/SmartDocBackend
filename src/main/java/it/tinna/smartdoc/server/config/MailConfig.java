package it.tinna.smartdoc.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import it.tinna.smartdoc.service.mail.MailSenderService;

@Configuration
public class MailConfig {

    @Value("${smartdoc.mail.from}")
    private String from;

    @Value("${smartdoc.mail.fromName}")
    private String fromName;

    @Value("${smartdoc.mail.to.fastorder}")
    private String toFastOrder;

    @Value("${smartdoc.mail.to.justdesign}")
    private String toJustDesign;

    /**
     * Bean per il servizio mail specifico di FastOrder.
     * Utilizza il JavaMailSender autoconfigurato da Spring Boot tramite application.properties.
     */
    @Bean(name = "mailSenderServiceFastOrder")
    public MailSenderService mailSenderServiceFastOrder(JavaMailSender mailSender) {
        MailSenderService service = new MailSenderService();
        service.setMailSender(mailSender);
        service.setFrom(from);
        service.setFromName(fromName);
        service.setTo(new String[]{toFastOrder});
        return service;
    }

    /**
     * Bean per il servizio mail specifico di JustDesign.
     */
    @Bean(name = "mailSenderServiceJustDesign")
    public MailSenderService mailSenderServiceJustDesign(JavaMailSender mailSender) {
        MailSenderService service = new MailSenderService();
        service.setMailSender(mailSender);
        service.setFrom(from);
        service.setFromName(fromName);
        service.setTo(new String[]{toJustDesign});
        return service;
    }
}
