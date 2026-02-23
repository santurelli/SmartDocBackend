package it.tinna.smartdoc.batch.tasklet;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;

import it.tinna.smartdoc.batch.constants.BatchConstants;
import it.tinna.smartdoc.batch.dto.DatiFatturaInviataSdiDto;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.documenti.StatoFatturaElettronica;
import lombok.Setter;

public class AggiornaFattureInviateTasklet implements Tasklet
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Setter
    private String                     dbKey;

    @Setter
    private int                        test;

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @SuppressWarnings("unchecked")
    @Override
    public RepeatStatus execute(StepContribution contribution,
                                ChunkContext chunkContext) throws Exception
    {
        List<DatiFatturaInviataSdiDto> datiFattureList = (List<DatiFatturaInviataSdiDto>) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get(BatchConstants.EXECUTIONCONTEXT_ELENCO_FATTURE);
        try
        {
            if ( datiFattureList != null )
            {
                for ( DatiFatturaInviataSdiDto dfiDto : datiFattureList )
                {
                    fatturaelettronicaDelegate.aggiornaStatoFattura(dfiDto.getIdFattura(), StatoFatturaElettronica.IN);
                    logger.info("Aggiornata la fattura {} come inviata allo SDI", dfiDto.getIdFattura());
                }
            }
        }
        finally
        {
            DatabaseContextHolder.clear();
        }
        return RepeatStatus.FINISHED;
    }

}

