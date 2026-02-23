package it.tinna.smartdoc.batch.writers.esitiinvio;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.springframework.batch.item.Chunk;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.beans.factory.annotation.Autowired;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.server.xml.fattura.sdi.quadratura.v2_0.jaxbClass.EsitoFTPType;
import lombok.Setter;

public class EsitiInvioWriter implements ItemStreamWriter<File>
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     dbKey;

    @Setter
    private StepExecution              stepExecution;

    @Setter
    private String                     workingFolder;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

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

    @Override
    public void write(Chunk<? extends File> arg0) throws Exception
    {
        String workFolder = stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR);
        for ( File file : arg0 )
        {
            InputStream inputStream = new FileInputStream(file);
            StreamSource source = new StreamSource(inputStream);
            JAXBElement<EsitoFTPType> esitoInvio = null;
            try
            {
                JAXBContext jaxbContext = JAXBContext.newInstance(EsitoFTPType.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                esitoInvio = jaxbUnmarshaller.unmarshal(source, EsitoFTPType.class);
                fatturaelettronicaDelegate.aggiornaDatiInvioSupporto(esitoInvio.getValue());
            }
            catch ( JAXBException e3 )
            {
                logger.error("Errore nell unmarshalling del file dell'esito di invio allo sdi {}", file.getAbsolutePath(), e3);
                throw new ItemStreamException(e3);
            }
            finally
            {
                inputStream.close();
            }

            System.out.println(file.getAbsolutePath());
        }
    }

}

