package it.tinna.smartdoc.batch.writers.esitisdi;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FattureFornitoreDelegate;
import it.tinna.smartdoc.server.delegate.documenti.NoteCreditoFornitoreDelegate;
import it.tinna.smartdoc.server.delegate.municipality.MunicipalityDelegate;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.MetadatiInvioFileType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.NotificaMancataConsegnaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.NotificaScartoType;
import it.tinna.smartdoc.server.xml.fattura.sdi.messaggitypes.v1_1.jaxbClass.RicevutaConsegnaType;
import it.tinna.smartdoc.server.xml.fattura.sdi.v1_2.jaxbClass.FatturaElettronicaType;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.municipality.MunicipalityDto;
import lombok.Setter;

public class EsitiSdiWriter implements ItemStreamWriter<File>, StepExecutionListener
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     comandoVerificaFirma;

//    @Setter
//    private String                     dbKey;

    @Setter
    private StepExecution              stepExecution;

    @Setter
    private String                     workingFolder;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Autowired
    private MunicipalityDelegate       municipalityDelegate;

    private List<FatturaDto>           fatture;

    @Autowired
    private FattureFornitoreDelegate   fattureFornitoreDelegate;

    @Autowired
    private NoteCreditoFornitoreDelegate noteCreditoFornitoreDelegate;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution)
    {
//        DatabaseContextHolder.set(dbKey);
        return ExitStatus.COMPLETED;
    }

    /**
     * Cerca di fare il parsing del file xml nel caso in cui sia una notifica di scarto
     */
    private void analizzaMetadatiInvioFile(File fileSdi,
                                           StreamSource source)
    {
        JAXBElement<MetadatiInvioFileType> metadatiInvioFile = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(MetadatiInvioFileType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            metadatiInvioFile = jaxbUnmarshaller.unmarshal(source, MetadatiInvioFileType.class);
            String nomeFile = metadatiInvioFile.getValue().getNomeFile();
            File fileFirmaVerificata = new File(FilenameUtils.getFullPath(fileSdi.getAbsolutePath()), FilenameUtils.getBaseName(nomeFile));
            try
            {
                if ( FilenameUtils.getExtension(nomeFile).equalsIgnoreCase("p7m") )
                {
                    CMSSignedData cms = new CMSSignedData(FileUtils.readFileToByteArray(new File(FilenameUtils.getFullPath(fileSdi.getAbsolutePath()), nomeFile)));
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    cms.getSignedContent().write(out);
                    FileUtils.writeByteArrayToFile(fileFirmaVerificata, out.toByteArray());
                }
                else
                {
                    fileFirmaVerificata = new File(FilenameUtils.getFullPath(fileSdi.getAbsolutePath()), nomeFile);
                }

                // recupero la partita iva dell'azienda
                try
                {
                    jaxbContext = JAXBContext.newInstance(FatturaElettronicaType.class);
                    jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                    JAXBElement<FatturaElettronicaType> root = jaxbUnmarshaller.unmarshal(new StreamSource(fileFirmaVerificata), FatturaElettronicaType.class);
                    FatturaElettronicaType fattura = root.getValue();
                    TipoDocumentoEnum tipoDocumento = fattura.getFatturaElettronicaBody().get(0).getDatiGenerali().getDatiGeneraliDocumento().getTipoDocumento();
                    String partitaIva = fattura.getFatturaElettronicaHeader().getCessionarioCommittente().getDatiAnagrafici().getIdFiscaleIVA().getIdCodice();
                    try
                    {
                        MunicipalityDto aziendaDto = municipalityDelegate.getByPartitaIva(partitaIva);
                        if ( aziendaDto != null )
                        {
                            byte[] fileBytes = FileUtils.readFileToByteArray(fileFirmaVerificata);
                            DatabaseContextHolder.set(aziendaDto.getDbName());
                            try {
                                if ( tipoDocumento == TipoDocumentoEnum.NOTA_CREDITO )
                                {
                                    noteCreditoFornitoreDelegate.importFromSdi(fileBytes);
                                }
                                else
                                {
                                    fattureFornitoreDelegate.importFromSdi(fileBytes);
                                }
                            } catch (Exception e) {
                                logger.error("Errore nell'importazione da SDI del documento fornitore", e);
                            } finally {
                                DatabaseContextHolder.clear();
                            }
                        }
                        else
                        {
                            logger.error("Nessuna azienda cliente trovata con partita iva {}", partitaIva);
                        }
                    }
                    catch ( SQLException e )
                    {
                        logger.error("Errore nel recupero della partita iva dell'azienda", e);
                    }
                }
                catch ( JAXBException e )
                {
                    logger.error("Errore nell'unmarshalling del file della fattura elettronica per il recupero della partita iva dell'azienda", e);
                }
            }
            catch ( CMSException | IOException e )
            {
                logger.error("Errore nella creazione dell'oggetto cmsSignedData", e);
            }
        }
        catch ( JAXBException e3 )
        {
            logger.info("L'esito sdi {} non è una ricevuta di consegna", fileSdi.getAbsolutePath());
        }
    }

    /**
     * Cerca di fare il parsing del file xml nel caso in cui sia una notifica di mancata consegna
     */
    private void analizzaNotificaMancataConsegna(File fileSdi,
                                                 StreamSource source) throws JAXBException, SQLException
    {
        JAXBElement<NotificaMancataConsegnaType> notificaMancataConsegna = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(NotificaScartoType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            notificaMancataConsegna = jaxbUnmarshaller.unmarshal(source, NotificaMancataConsegnaType.class);
            fatturaelettronicaDelegate.aggiornaDatiEsitoSdi(fileSdi, notificaMancataConsegna.getValue());
        }
        catch ( JAXBException e3 )
        {
            logger.info("L'esito sdi {} non è una notifica di mancata consegna", fileSdi.getAbsolutePath());
            throw e3;
        }
    }

    /**
     * Cerca di fare il parsing del file xml nel caso in cui sia una notifica di scarto
     */
    private void analizzaNotificaScarto(File fileSdi,
                                        StreamSource source) throws JAXBException, SQLException
    {
        JAXBElement<NotificaScartoType> notificaScarto = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(NotificaScartoType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            notificaScarto = jaxbUnmarshaller.unmarshal(source, NotificaScartoType.class);
            fatturaelettronicaDelegate.aggiornaDatiEsitoSdi(fileSdi, notificaScarto.getValue());
        }
        catch ( JAXBException e3 )
        {
            logger.info("L'esito sdi {} non è una ricevuta di consegna", fileSdi.getAbsolutePath());
            throw e3;
        }
    }

    /**
     * Cerca di fare il parsing del file xml nel caso in cui sia una ricevuta di consegna
     */
    private void analizzaRicevutaConsegna(File fileSdi,
                                          StreamSource source) throws JAXBException, SQLException
    {
        JAXBElement<RicevutaConsegnaType> ricevutaConsegna = null;
        try
        {
            JAXBContext jaxbContext = JAXBContext.newInstance(RicevutaConsegnaType.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            ricevutaConsegna = jaxbUnmarshaller.unmarshal(source, RicevutaConsegnaType.class);
            fatturaelettronicaDelegate.aggiornaDatiEsitoSdi(fileSdi, ricevutaConsegna.getValue());
        }
        catch ( JAXBException e3 )
        {
            logger.info("L'esito sdi {} non è una ricevuta di consegna", fileSdi.getAbsolutePath());
            throw e3;
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution)
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() throws ItemStreamException
    {

    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {

    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void write(org.springframework.batch.item.Chunk<? extends java.io.File> arg0) throws Exception
    {
        if ( stepExecution.getJobExecution().getExecutionContext().containsKey(BatchConstants.EXECUTIONCONTEXT_FATTURE_ESITO_SDI) )
        {
            fatture = (List<it.tinna.smartdoc.shared.dto.documenti.FatturaDto>) stepExecution.getJobExecution().getExecutionContext().get(BatchConstants.EXECUTIONCONTEXT_FATTURE_ESITO_SDI);
        }
        else
        {
            fatture = new ArrayList<>();
            stepExecution.getJobExecution().getExecutionContext().put(BatchConstants.EXECUTIONCONTEXT_FATTURE_ESITO_SDI, fatture);
        }
        String workFolder = stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR);
        for ( File file : arg0 )
        {
            File folder = new File(workFolder, FilenameUtils.getBaseName(file.getAbsolutePath()));
            FileUtils.forceMkdir(folder);
            logger.info("Analizzo il file {}", file.getAbsolutePath());
            ZipFile zipFile = new ZipFile(file);
            try
            {
                final Enumeration<? extends ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements())
                {
                    final ZipEntry entry = entries.nextElement();
                    System.out.printf("File: %s Size %d Modified on %TD %n", entry.getName(), entry.getSize(), new Date(entry.getTime()));
                    FileUtils.copyInputStreamToFile(zipFile.getInputStream(entry), new File(folder, entry.getName()));
                }
            }
            finally
            {
                zipFile.close();
            }
            IOFileFilter filter = FileFilterUtils.notFileFilter(FileFilterUtils.prefixFileFilter("FO", IOCase.INSENSITIVE));
            List<File> elencoFile = new ArrayList<File>(FileUtils.listFiles(folder, filter, null));
            if ( elencoFile != null && !elencoFile.isEmpty() )
            {
                for ( File fileSdi : elencoFile )
                {
                    InputStream inputStream = new FileInputStream(fileSdi);
                    StreamSource source = new StreamSource(inputStream);
                    if ( StringUtils.containsIgnoreCase(FilenameUtils.getBaseName(fileSdi.getAbsolutePath()), "_RC_") )
                    {
                        try
                        {
                            analizzaRicevutaConsegna(fileSdi, source);
                        }
                        catch ( JAXBException e )
                        {
                            logger.error("Errore nell'unmarshalling della ricevuta di consegna {}", fileSdi.getAbsolutePath(), e);
                        }
                    }
                    else if ( StringUtils.containsIgnoreCase(FilenameUtils.getBaseName(fileSdi.getAbsolutePath()), "_NS_") )
                    {
                        try
                        {
                            analizzaNotificaScarto(fileSdi, source);
                        }
                        catch ( JAXBException e )
                        {
                            logger.error("Errore nell'unmarshalling della notifica di scarto {}", fileSdi.getAbsolutePath(), e);
                        }
                    }
                    else if ( StringUtils.containsIgnoreCase(FilenameUtils.getBaseName(fileSdi.getAbsolutePath()), "_MT_") )
                    {
                        analizzaMetadatiInvioFile(fileSdi, source);
                    }
                    else if ( StringUtils.containsIgnoreCase(FilenameUtils.getBaseName(fileSdi.getAbsolutePath()), "_MC_") )
                    {
                        analizzaNotificaMancataConsegna(fileSdi, source);
                    }
                }
            }
            System.out.println(file.getAbsolutePath());
        }
    }

}

