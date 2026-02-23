package it.tinna.smartdoc.batch.processors.esitisdi;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import lombok.Setter;

public class EsitiSdiProcessor implements ItemProcessor<File, File>
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     comandoDecifratura;

    @Setter
    private String                     comandoVerificaFirma;

    // private String decryptCommand = "openssl smime -decrypt -in /home/sdi_fattura/DatiDaSdITest/TestDaSogei_snp.p7m.enc -inform der -binary -out /home/sogei.decript -recip /home/tomcat/sogei/CERTS/SNTPTR75S11I726G.SNPAT001.CIFRA.PEM -passin pass:KiyD3dMZ1JML1WSz";
    //
    // private String verificaFirmaCommand = "openssl smime -verify -in /home/sogei.decript -inform der -binary -out /home/sogei -CAfile /home/tomcat/sogei/CERTS/CAEntrate.pem";

    @Setter
    private StepExecution              stepExecution;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Override
    public File process(File arg0) throws Exception
    {
        File workDir = new File(stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR));
        File fileDecifrato = new File(workDir, FilenameUtils.getBaseName(arg0.getAbsolutePath()));
        fileDecifrato.createNewFile();
        Map<String, Object> model = new HashMap<>();
        model.put("source", new File(workDir, FilenameUtils.getName(arg0.getAbsolutePath())).getAbsolutePath());
        model.put("destination", fileDecifrato.getAbsolutePath());
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        cfg.setTemplateLoader(stringLoader);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        stringLoader.putTemplate("comandoDecifratura", comandoDecifratura);
        Writer out = new StringWriter();
        DefaultExecutor executor = new DefaultExecutor();
        try
        {
            Template template1 = cfg.getTemplate("comandoDecifratura");
            template1.process(model, out);
            logger.info("Comando di decifratura esito invio SDI generato: {}", out.toString());
            CommandLine cmdLine = CommandLine.parse(out.toString());
            try
            {
                int exitValue = executor.execute(cmdLine);
                logger.info("Comando di decifratura esito SDI {} terminato con codice di uscita {}", out.toString(), exitValue);
                File fileFirmaVerificata = new File(workDir, FilenameUtils.getBaseName(fileDecifrato.getAbsolutePath()));
                model = new HashMap<>();
                model.put("source", fileDecifrato.getAbsolutePath());
                model.put("destination", fileFirmaVerificata.getAbsolutePath());
                cfg = new Configuration(Configuration.VERSION_2_3_23);
                stringLoader = new StringTemplateLoader();
                cfg.setTemplateLoader(stringLoader);
                cfg.setDefaultEncoding("UTF-8");
                cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                stringLoader.putTemplate("comandoVerifica", comandoVerificaFirma);
                out = new StringWriter();
                try
                {
                    template1 = cfg.getTemplate("comandoVerifica");
                    template1.process(model, out);
                    logger.info("Comando di verifica firma esito SDI generato: {}", out.toString());
                    cmdLine = CommandLine.parse(out.toString());
                    try
                    {
                        exitValue = executor.execute(cmdLine);
                        logger.debug("Comando di verifica firma esito SDI {} terminato con codice di uscita {}", out.toString(), exitValue);
                        return fileFirmaVerificata;
                    }
                    catch ( IOException e )
                    {
                        logger.error("Errore nell'esecuzione del comando di verifica firma esito SDI {}", out.toString(), e);
                        throw e;
                    }
                }
                catch ( IOException e )
                {
                    logger.error("Errore nella generazione del comando di verifica firma esito SDI", e);
                    throw e;
                }
            }
            catch ( IOException e )
            {
                logger.error("Errore nell'esecuzione del comando di decifratura esito SDI {}", out.toString(), e);
                throw e;
            }
        }
        catch ( IOException e )
        {
            logger.error("Errore nella generazione del comando di decifratura esito SDI", e);
            throw new Exception(e);
        }

        // File workDir = new File(stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR));
        // File fileDecifrato = new File(workDir, FilenameUtils.getBaseName(arg0.getAbsolutePath()));
        // fileDecifrato.createNewFile();
        // StringBuilder strBCommand = new StringBuilder("\"C:/Program Files/OpenSSL/bin/openssl\" smime -decrypt -in ");
        // strBCommand.append(new File(workDir, FilenameUtils.getName(arg0.getAbsolutePath())).getAbsolutePath());
        // strBCommand.append(" -inform der -binary -out ");
        // strBCommand.append(fileDecifrato.getAbsolutePath());
        // strBCommand.append(" -recip F:/sogei/CERTS/SNTPTR75S11I726G.SNPAT001.CIFRA.PEM -passin pass:KiyD3dMZ1JML1WSz");
        // CommandLine cmdLine = CommandLine.parse(strBCommand.toString());
        // DefaultExecutor executor = new DefaultExecutor();
        // try
        // {
        // int exitValue = executor.execute(cmdLine);
        // logger.debug("Comando di decrifatura {} terminato con codice di uscita {}", strBCommand.toString(), exitValue);
        // // verifico firma
        // File fileFirmaVerificata = new File(workDir, FilenameUtils.getBaseName(fileDecifrato.getAbsolutePath()));
        // StringBuilder encodeCommand = new StringBuilder("\"C:/Program Files/OpenSSL/bin/openssl\" smime -verify -in ");
        // encodeCommand.append(fileDecifrato.getAbsolutePath());
        // encodeCommand.append(" -inform der -binary -out ");
        // encodeCommand.append(fileFirmaVerificata.getAbsolutePath());
        // encodeCommand.append(" -CAfile F:/sogei/CERTS/CAEntrate.pem");
        // cmdLine = CommandLine.parse(encodeCommand.toString());
        // try
        // {
        // exitValue = executor.execute(cmdLine);
        // logger.debug("Comando di verifica firma {} terminato con codice di uscita {}", encodeCommand.toString());
        // return fileFirmaVerificata;
        // }
        // catch ( IOException e )
        // {
        // logger.error("Errore nell'esecuzione del comando di verifica firma {}", encodeCommand.toString(), e);
        // throw e;
        // }
        // }
        // catch ( IOException e )
        // {
        // logger.error("Errore nell'esecuzione del comando di decifratura {}", strBCommand.toString(), e);
        // throw e;
        // }
    }

}

