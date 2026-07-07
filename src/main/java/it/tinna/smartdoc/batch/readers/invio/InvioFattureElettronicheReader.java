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
import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.server.delegate.documenti.FattureDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

public class InvioFattureElettronicheReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FattureDelegate                    fattureDelegate;

    @Autowired
    private FatturaElettronicaDelegate         fatturaElettronicaDelegate;

    @Setter
    private String                             dbKey;

    // CSV di id (es. "1,2,3") iniettato dal job parameter; null = tutte le fatture DA INVIARE
    private String                             idFattureParam;

    public void setIdFatture(String idFattureParam) {
        this.idFattureParam = idFattureParam;
    }

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
            long[] idFatture = null;
            if (org.apache.commons.lang3.StringUtils.isNotBlank(idFattureParam)) {
                String[] parts = idFattureParam.split(",");
                idFatture = new long[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    idFatture[i] = Long.parseLong(parts[i].trim());
                }
            }
            List<Long> list = fattureDelegate.getFattureElettronicheDaInviare(idFatture);
            logger.info("Trovate {} fatture elettroniche da elaborare", list.size());
            for (long idFattura : list)
            {
                // Controllo: se la fattura è già stata inviata e stiamo aspettando l'esito SDI,
                // non la reinviamo per evitare duplicati. La saltiamo finché non arriva la risposta.
                boolean inTransito = !fatturaElettronicaDelegate.isFatturaInviabile(dbKey, idFattura);
                if (inTransito) {
                    logger.info("Fattura {} già in transito presso SDI (esito non ancora ricevuto). Salto.", idFattura);
                    continue;
                }
                
                FatturaElettronicaWrapperDto dto = fattureDelegate.getFatturaElettronica(idFattura);
                elencoFatture.add(dto);
            }
            logger.info("{} fatture pronte per l'invio SDI (escluse quelle già in transito)", elencoFatture.size());
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

