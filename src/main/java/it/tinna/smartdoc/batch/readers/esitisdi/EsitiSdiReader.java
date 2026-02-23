package it.tinna.smartdoc.batch.readers.esitisdi;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import lombok.Setter;

public class EsitiSdiReader implements ItemStreamReader<File>
{

    private Logger        logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String        dbKey;

    @Setter
    private String        cartellaEsiti;

    private List<File>    elencoFile;

    @Setter
    private StepExecution stepExecution;

    @Override
    public void close() throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        IOFileFilter filter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter("FO", IOCase.INSENSITIVE), FileFilterUtils.suffixFileFilter(".enc", IOCase.INSENSITIVE));
        elencoFile = new ArrayList<File>(FileUtils.listFiles(new File(cartellaEsiti), filter, null));
        for ( File file : elencoFile )
        {
            try
            {
                FileUtils.moveFileToDirectory(file, new File(stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR)), false);
            }
            catch ( IOException e )
            {
                logger.error("Errore nello spostamento del file {} nella cartella {}", file.getAbsolutePath(), stepExecution.getJobExecution().getExecutionContext().getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR), e);
                throw new ItemStreamException(e);
            }
        }
        logger.info("Trovati {} file nella cartella {}", elencoFile.size(), cartellaEsiti);
    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public File read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if ( elencoFile.size() > 0 )
        {
            return elencoFile.remove(0);
        }
        else
        {
            return null;
        }
    }

}

