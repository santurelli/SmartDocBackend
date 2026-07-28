package it.tinna.smartdoc.batch.writers.invio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
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

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.batch.dto.DatiFatturaInviataSdiDto;
import it.tinna.smartdoc.server.constants.TipoDocumentoEnum;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

/**
 * Writer per le autofatture (TD17/TD18/TD19/TD20/TD28).
 * Usa la chiave ELENCO_AUTOFATTURE nell'execution context per non interferire
 * con la lista delle fatture attive.
 * Le autofatture sono sempre B2B (mai PA), quindi non richiedono firma XAdES.
 */
public class InvioAutofattureWriter implements ItemStreamWriter<FatturaElettronicaWrapperDto>, StepExecutionListener
{

    private Logger        logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String        dbKey;

    @Setter
    private StepExecution stepExecution;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution)
    {
        DatabaseContextHolder.clear();
        return ExitStatus.COMPLETED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {}

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException {}

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException {}

    @Override
    public void write(Chunk<? extends FatturaElettronicaWrapperDto> chunk) throws Exception
    {
        List<DatiFatturaInviataSdiDto> datiFattureList;
        ExecutionContext execCtx = stepExecution.getJobExecution().getExecutionContext();

        if ( !execCtx.containsKey(BatchConstants.EXECUTIONCONTEXT_ELENCO_AUTOFATTURE) )
        {
            datiFattureList = new ArrayList<>();
            execCtx.put(BatchConstants.EXECUTIONCONTEXT_ELENCO_AUTOFATTURE, datiFattureList);
        }
        else
        {
            datiFattureList = (List<DatiFatturaInviataSdiDto>) execCtx.get(BatchConstants.EXECUTIONCONTEXT_ELENCO_AUTOFATTURE);
        }

        String workFolder = execCtx.getString(BatchConstants.EXECUTIONCONTEXT_JOBDIR);

        for ( FatturaElettronicaWrapperDto dto : chunk )
        {
            if ( StringUtils.isBlank(dto.getFattura().getErroreValidazioneXml()) )
            {
                DatiFatturaInviataSdiDto dfiDto = new DatiFatturaInviataSdiDto();
                dfiDto.setProgressivoFile(dto.getProgressivoFile());
                dfiDto.setIdFattura(dto.getFattura().getId());
                dfiDto.setNomePacchetto(dto.getNomeFileFattura());
                dfiDto.setIdFatturaElettronica(dto.getIdFatturaElettronica());
                dfiDto.setTipoDocumento(TipoDocumentoEnum.AUTOFATTURA);
                datiFattureList.add(dfiDto);

                File f = new File(workFolder, dto.getNomeFileFattura());
                FileUtils.writeByteArrayToFile(f, dto.getFlussoFatturaElettronica());
                // Autofatture: sempre B2B, nessuna firma PA richiesta
                FileUtils.copyFile(f, new File(new StringBuilder(workFolder)
                        .append(File.separator).append("firmati").toString(),
                        FilenameUtils.getName(f.getAbsolutePath())));

                logger.info("Autofattura {} scritta su disco: {}", dto.getFattura().getId(), dto.getNomeFileFattura());
            }
        }
    }

}
