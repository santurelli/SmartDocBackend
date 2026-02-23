package it.tinna.smartdoc.batch.tasklet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.batch.dto.DatiFatturaInviataSdiDto;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.configurazione.ConfigurazioneDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.NumeroFileType;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.QuadraturaFTPType;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.TipoFileType;
import lombok.Setter;

public class InviaSupportiSdiTasklet implements Tasklet, StepExecutionListener
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     cartellaOutput;

    @Setter
    private String                     comandoCifratura;

    @Setter
    private String                     comandoFirma;

    @Setter
    private String                     dbKey;

    @Setter
    private StepExecution              stepExecution;

    @Setter
    private int                        test;

    @Autowired
    private ConfigurazioneDelegate     configurazioneDelegate;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    /*
     * Questo metodo è FONDAMENTALE per impostare la chiave db ed eseguire l'update dello stato fatture ad Inviate allo SDI. E' necessario impostare qui la chiave db perchè la classe FatturaElettronicaDelegate è autowired e quindi non ho modo di impostare la chiave db prima del recupero dell'oggetto dal contesto
     */
    @Override
    public ExitStatus afterStep(StepExecution stepExecution)
    {
        DatabaseContextHolder.set(dbKey);
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution)
    {
        DatabaseContextHolder.clear();
        try
        {
            comandoFirma = configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_BATCH_INVIO_SDI, BatchConstants.CONFIG_KEY_COMANDO_FIRMA);
        }
        catch ( SQLException e )
        {
        }
        Assert.notNull(comandoFirma, "Il comando di firma non può essere null");
        try
        {
            comandoCifratura = configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_BATCH_INVIO_SDI, BatchConstants.CONFIG_KEY_COMANDO_CIFRATURA);
        }
        catch ( SQLException e )
        {
        }
        try
        {
            cartellaOutput = configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_BATCH_INVIO_SDI, BatchConstants.CONFIG_KEY_CARTELLA_OUTPUT);
        }
        catch ( SQLException e )
        {
        }
        try
        {
            test = Integer.parseInt(configurazioneDelegate.getByKey(BatchConstants.CONFIG_DOMAIN_BATCH, BatchConstants.CONFIG_KEY_TEST));
        }
        catch ( SQLException e )
        {
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception
    {
        List<DatiFatturaInviataSdiDto> datiFattureList = (List<DatiFatturaInviataSdiDto>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get(BatchConstants.EXECUTIONCONTEXT_ELENCO_FATTURE);
        if ( datiFattureList != null && !datiFattureList.isEmpty() )
        {
            String workDir = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR);
            File dainviare = new File(workDir, "dainviare");
            dainviare.mkdir();
            StringBuilder strB = new StringBuilder("FI.").append(BatchConstants.IDENTIFICATIVO_NODO).append(".");
            long supportiInviati = 0L;
            try
            {
                DatabaseContextHolder.set(BatchConstants.DB_KEY_SERVICE_DB);
                supportiInviati = fatturaelettronicaDelegate.getSupportiInviati();
            }
            finally
            {
                DatabaseContextHolder.clear();
            }
            if ( test != 0 )
            {
                supportiInviati += 900;
            }
            Date dtGenerazione = new Date();
            strB.append(FastDateFormat.getInstance("yyyyDDD").format(dtGenerazione)).append(".").append(FastDateFormat.getInstance("HHmm").format(dtGenerazione)).append(".").append((StringUtils.leftPad(Long.toString(supportiInviati + 1), 3, "0"))).append(".zip");
            File f = new File(workDir, strB.toString());
            FileOutputStream fos = new FileOutputStream(f);
            ZipOutputStream zos = new ZipOutputStream(fos);
            Collection<File> files = FileUtils.listFiles(new File(new StringBuilder(workDir).append(File.separator).append("firmati").toString()), FileFilterUtils.or(FileFilterUtils.suffixFileFilter(".p7m"), FileFilterUtils.suffixFileFilter(".xml")), null);
            for ( File file : files )
            {
                ZipEntry ze = new ZipEntry(FilenameUtils.getName(file.getAbsolutePath()));
                zos.putNextEntry(ze);
                zos.write(FileUtils.readFileToByteArray(file));
            }
            QuadraturaFTPType quadratura = new QuadraturaFTPType();
            quadratura.setVersione("2.0");
            quadratura.setIdentificativoNodo(BatchConstants.IDENTIFICATIVO_NODO);
            quadratura.setDataOraCreazione(dtGenerazione);
            quadratura.setNomeSupporto(FilenameUtils.getName(f.getAbsolutePath()));
            NumeroFileType numeroFile = new NumeroFileType();
            TipoFileType tipoFile = new TipoFileType();
            tipoFile.setTipo("FA");
            tipoFile.setNumero(files.size());
            numeroFile.getFile().add(tipoFile);
            quadratura.setNumeroFile(numeroFile);

            JAXBContext jaxbContext = JAXBContext.newInstance(QuadraturaFTPType.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            QName qName = new QName("http://www.fatturapa.it/sdi/ftp/v2.0", "FileQuadraturaFTP");
            JAXBElement<QuadraturaFTPType> je = new JAXBElement<>(qName, QuadraturaFTPType.class, quadratura);

            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Resource resource = new ClassPathResource("xsd/sdi/quadratura_2_0/FtpTypes_v2.0.xsd");
            File fileQuadratura = null;
            StringWriter sw = null;
            try
            {
                Source schemaSource = new StreamSource(resource.getInputStream());
                Schema schema = sf.newSchema(schemaSource);
                Validator validator = schema.newValidator();

                sw = new StringWriter();
                jaxbMarshaller.marshal(je, sw);
                fileQuadratura = new File(FilenameUtils.getFullPathNoEndSeparator(f.getAbsolutePath()), FilenameUtils.getBaseName(f.getAbsolutePath()) + ".xml");
                FileUtils.writeStringToFile(fileQuadratura, sw.toString(), "UTF8");

                InputStream inputStream = new FileInputStream(fileQuadratura);
                StreamSource source = new StreamSource(inputStream);
                validator.validate(source);

                ZipEntry ze = new ZipEntry(FilenameUtils.getName(fileQuadratura.getAbsolutePath()));
                zos.putNextEntry(ze);
                zos.write(FileUtils.readFileToByteArray(fileQuadratura));
            }
            catch ( SAXException | IOException e )
            {
                logger.error(new StringBuilder("Validazione file quadratura del supporto ").append(f.getAbsolutePath()).append(" fallita").toString(), e);
                throw new Exception(e);
            }
            finally
            {
                zos.close();
                fos.close();
            }

            FileUtils.deleteQuietly(fileQuadratura);

            DefaultExecutor executor = new DefaultExecutor();

            File fileFirmato = new File(FilenameUtils.getFullPath(f.getAbsolutePath()), new StringBuilder(FilenameUtils.getBaseName(f.getAbsolutePath())).append(".signed").toString());
            fileFirmato.createNewFile();
            logger.info("Template comando firma: {}", comandoFirma);
            Map<String, Object> model = new HashMap<>();
            model.put("source", f.getAbsolutePath());
            model.put("destination", fileFirmato.getAbsolutePath());
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
            StringTemplateLoader stringLoader = new StringTemplateLoader();
            cfg.setTemplateLoader(stringLoader);
            cfg.setDefaultEncoding("UTF-8");
            cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
            stringLoader.putTemplate("comandoFirma", comandoFirma);
            Writer out = new StringWriter();
            try
            {
                Template template1 = cfg.getTemplate("comandoFirma");
                template1.process(model, out);
                logger.info("Comando di firma generato: {}", out.toString());
                CommandLine cmdLine = CommandLine.parse(out.toString());
                try
                {
                    int exitValue = executor.execute(cmdLine);
                    logger.info("Comando di firma {} terminato con codice di uscita {}", out.toString(), exitValue);
                    // cifro il file firmato
                    File fileCifrato = new File(dainviare, FilenameUtils.getName(f.getAbsolutePath()));
                    fileCifrato.createNewFile();

                    model = new HashMap<>();
                    model.put("source", fileFirmato.getAbsolutePath());
                    model.put("destination", fileCifrato.getAbsolutePath());
                    cfg = new Configuration(Configuration.VERSION_2_3_23);
                    stringLoader = new StringTemplateLoader();
                    cfg.setTemplateLoader(stringLoader);
                    cfg.setDefaultEncoding("UTF-8");
                    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                    stringLoader.putTemplate("comandoCifratura", comandoCifratura);
                    out = new StringWriter();
                    try
                    {
                        template1 = cfg.getTemplate("comandoCifratura");
                        template1.process(model, out);
                        logger.info("Comando di cifratura generato: {}", out.toString());
                        cmdLine = CommandLine.parse(out.toString());
                        try
                        {
                            exitValue = executor.execute(cmdLine);
                            logger.debug("Comando di cifratura {} terminato con codice di uscita {}", out.toString(), exitValue);
                            try
                            {
                                DatabaseContextHolder.set(BatchConstants.DB_KEY_SERVICE_DB);
                                long idSupporto = fatturaelettronicaDelegate.memorizzaSupporto(fileCifrato, files.size());

                                for ( DatiFatturaInviataSdiDto dfiDto : datiFattureList )
                                {
                                    fatturaelettronicaDelegate.memorizzaInvioSdi(dfiDto.getIdFatturaElettronica(), dfiDto.getProgressivoFile(), idSupporto);
                                    if ( !StringUtils.containsIgnoreCase(dbKey, "justdesign") )
                                    {
                                        if ( dfiDto.getTipoDocumento() == TipoDocumentoEnum.FATTURA )
                                        {
                                            fatturaelettronicaDelegate.impostaInviataSdi(dbKey, dfiDto.getIdFattura());
                                        }
                                        else
                                        {
                                            fatturaelettronicaDelegate.impostaNotaCreditoInviataSdi(dbKey, dfiDto.getIdFattura());
                                        }
                                    }
                                }
                            }
                            finally
                            {
                                DatabaseContextHolder.clear();
                            }
                            // Files.copy(FileSystems.getDefault().getPath(fileCifrato.getAbsolutePath()), FileSystems.getDefault().getPath(new File(cartellaOutput, FilenameUtils.getName(fileCifrato.getAbsolutePath())).getAbsolutePath()), StandardCopyOption.COPY_ATTRIBUTES);
                            if ( SystemUtils.IS_OS_LINUX )
                            {
                                Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-rw-r--");
                                // FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(ownerWritable);
                                Files.setPosixFilePermissions(fileCifrato.toPath(), ownerWritable);
                                logger.info("Sposto il supporto firmato e cifrato tramite comando linux");
                                String comandoSpostamento = new StringBuilder("cp -a ").append(fileCifrato.getAbsolutePath()).append(" ").append(cartellaOutput).toString();
                                logger.debug("Comando spostamento supporto cifrato generato: {}", comandoSpostamento);
                                cmdLine = CommandLine.parse(comandoSpostamento);
                                try
                                {
                                    exitValue = executor.execute(cmdLine);
                                    logger.debug("Comando di spostamento supporto cifrato {} terminato con codice di uscita {}", comandoSpostamento, exitValue);
                                }
                                catch ( IOException e )
                                {
                                    logger.error("Errore nell'esecuzione del comando di spostamento del supporto cifrato {}", comandoSpostamento, e);
                                    throw e;
                                }
                            }
                            else
                            {
                                try
                                {
                                    FileUtils.copyFileToDirectory(fileCifrato, new File(cartellaOutput), true);
                                }
                                catch ( IOException e )
                                {
                                    logger.error("Errore nello spostamento del supporto {} nella cartella di destinazione", fileCifrato.getAbsoluteFile(), e);
                                    throw e;
                                }
                            }
                        }
                        catch ( IOException e )
                        {
                            logger.error("Errore nell'esecuzione del comando di cifratura {}", out.toString(), e);
                            throw e;
                        }
                    }
                    catch ( IOException e )
                    {

                    }
                }
                catch ( IOException e )
                {
                    logger.error("Errore nell'esecuzione del comando di firma {}", out.toString(), e);
                    throw e;
                }
            }
            catch ( IOException e )
            {
                logger.error("Errore nella generazione del comando di firma", e);
                throw new Exception(e);
            }
        }
        return RepeatStatus.FINISHED;
    }

}

