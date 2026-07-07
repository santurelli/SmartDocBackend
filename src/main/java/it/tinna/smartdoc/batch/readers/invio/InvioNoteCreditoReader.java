package it.tinna.smartdoc.batch.readers.invio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.NoteCreditoDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

public class InvioNoteCreditoReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NoteCreditoDelegate                notecreditoDelegate;

    @Setter
    private String                             dbKey;

    private String                             idFattureParam;

    public void setIdFatture(String idFattureParam) {
        this.idFattureParam = idFattureParam;
    }

    private List<FatturaElettronicaWrapperDto> elencoNoteCredito;

    @Override
    public void close() throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        elencoNoteCredito = new ArrayList<FatturaElettronicaWrapperDto>();
        try
        {
            DatabaseContextHolder.set(dbKey);
            long[] idFatture = null;
            if (org.apache.commons.lang3.StringUtils.isNotBlank(idFattureParam)) {
                String[] parts = idFattureParam.split(",");
                idFatture = new long[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    idFatture[i] = Long.parseLong(parts[i].trim());
                }
            }
            List<Long> list = notecreditoDelegate.getNoteCreditoDaInviare(idFatture);
            logger.info("Trovate {} note credito", list.size());
            for (long idFattura : list)
            {
                FatturaElettronicaWrapperDto dto = notecreditoDelegate.getFatturaElettronica(idFattura);
                elencoNoteCredito.add(dto);
            }
        }
        catch ( SQLException e )
        {
            throw new ItemStreamException(e);
        }
        finally
        {
            DatabaseContextHolder.clear();
        }

    }

    @Override
    public void update(ExecutionContext arg0) throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public FatturaElettronicaWrapperDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if ( elencoNoteCredito.size() > 0 )
        {
            return elencoNoteCredito.remove(0);
        }
        else
        {
            return null;
        }
    }

}

