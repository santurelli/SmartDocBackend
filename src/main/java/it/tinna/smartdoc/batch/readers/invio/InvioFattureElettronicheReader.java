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
import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

public class InvioFattureElettronicheReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FattureDelegate                    fattureDelegate;

    @Setter
    private String                             dbKey;

    @Setter
    private long[]                             idFatture;

    private List<FatturaElettronicaWrapperDto> elencoFatture;

    @Override
    public void close() throws ItemStreamException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        elencoFatture = new ArrayList<FatturaElettronicaWrapperDto>();
        try
        {
            DatabaseContextHolder.set(dbKey);
            List<Long> list = fattureDelegate.getFattureElettronicheDaInviare(idFatture);
            logger.info("Trovate {} fatture", list.size());
            for (long idFattura : list)
            {
                FatturaElettronicaWrapperDto dto = fattureDelegate.getFatturaElettronica(idFattura);
                elencoFatture.add(dto);
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
        if ( elencoFatture.size() > 0 )
        {
            return elencoFatture.remove(0);
        }
        else
        {
            return null;
        }
    }

}

