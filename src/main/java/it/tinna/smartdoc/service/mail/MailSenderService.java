package it.tinna.smartdoc.service.mail;

import java.util.Map;

import jakarta.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import it.tinna.smartdoc.shared.dto.mail.MailAttachmentDto;
import lombok.Setter;

public class MailSenderService
{

    // private Logger _log = LoggerFactory.getLogger(MailSenderService.class);

    @Setter
    private String[]       ccn;

    private String         from;

    @Setter
    private String         fromName;

    private JavaMailSender mailSender;

    private String[]       to;

    public void send(final String subject,
                     final String text)
    {
        send(subject, text, null, null);
    }

    public void send(final String subject,
                     final String text,
                     final MailAttachmentDto attachment,
                     final Map<String, Resource> inlineImages) throws MailException
    {
        MimeMessagePreparator preparator = new MimeMessagePreparator()
        {
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, attachment == null && inlineImages == null ? false : true, "UTF-8");
                message.setSubject(subject);
                message.setTo(to);
                if ( StringUtils.isBlank(fromName) )
                {
                    message.setFrom(from);
                }
                else
                {
                    message.setFrom(from, fromName);
                }
                if ( ccn != null && ccn.length != 0 )
                {
                    message.setBcc(ccn);
                }
                message.setText(text, true);
                if ( inlineImages != null && !inlineImages.isEmpty() )
                {
                    for ( Map.Entry<String, Resource> entry : inlineImages.entrySet() )
                    {
                        message.addInline(entry.getKey(), entry.getValue());
                    }
                }
                if ( attachment != null )
                {
                    FileSystemResource file = new FileSystemResource(attachment.getFile());
                    message.addAttachment(attachment.getName(), file);
                }
            }
        };
        mailSender.send(preparator);
    }

//    public void setCcn(String[] ccn)
//    {
//        if ( ccn != null )
//        {
//            this.ccn = ccn;
//        }
//        else
//        {
//            this.ccn = new String[0];
//        }
//    }

    public void setFrom(String from)
    {
        this.from = StringUtils.defaultIfBlank(from, StringUtils.EMPTY);
    }

    public void setMailSender(JavaMailSender mailSender)
    {
        this.mailSender = mailSender;
    }

    public void setTo(String[] to)
    {
        if ( to != null )
        {
            this.to = to;
        }
        else
        {
            this.to = new String[0];
        }
    }

}

