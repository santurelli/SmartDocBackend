package it.tinna.smartdoc.batch.readers.invio;

import java.nio.charset.StandardCharsets;
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

import it.tinna.smartdoc.server.dao.documenti.FattureFornitoreDao;
import it.tinna.smartdoc.server.database.DatabaseContextHolder;
import it.tinna.smartdoc.server.delegate.documenti.AutofatturaDelegate;
import it.tinna.smartdoc.shared.dto.clienti.TipologiaClienteFornitore;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.clienti.BaseClienteDto;
import lombok.Setter;

public class InvioAutofattureReader implements ItemStreamReader<FatturaElettronicaWrapperDto>
{

    private Logger                             logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AutofatturaDelegate                autofatturaDelegate;

    @Setter
    private String                             dbKey;

    private List<FatturaElettronicaWrapperDto> elencoAutofatture;

    @Override
    public void close() throws ItemStreamException {}

    @Override
    public void open(ExecutionContext arg0) throws ItemStreamException
    {
        elencoAutofatture = new ArrayList<>();
        try
        {
            DatabaseContextHolder.set(dbKey);
            FattureFornitoreDao dao = new FattureFornitoreDao(autofatturaDelegate.getJdbcTemplate());
            List<Long> ids = dao.getAutofattureDaInviare();
            logger.info("Trovate {} autofatture da inviare", ids.size());

            for (long id : ids)
            {
                try
                {
                    String xml = autofatturaDelegate.generaXml(id);
                    FatturaFornitoreDto fatturaFornitore = dao.getById(id);

                    FatturaElettronicaWrapperDto wrapper = new FatturaElettronicaWrapperDto();
                    wrapper.setFlussoFatturaElettronica(xml.getBytes(StandardCharsets.UTF_8));

                    // Costruiamo un FatturaElettronicaDto minimale per compatibilità con writer e processor
                    FatturaElettronicaDto fatturaDto = new FatturaElettronicaDto();
                    fatturaDto.setId((int) id);
                    fatturaDto.setNumDocumento(fatturaFornitore.getNumDocumento());
                    fatturaDto.setDataDocumento(fatturaFornitore.getDataDocumento());

                    // Il writer usa clienteDto.getTipologia() per decidere la firma PA — autofatture sono sempre PRIVATO
                    it.tinna.smartdoc.shared.dto.clienti.ClienteDto clienteDto = new it.tinna.smartdoc.shared.dto.clienti.ClienteDto();
                    clienteDto.setTipologia(TipologiaClienteFornitore.PRIVATO);
                    fatturaDto.setClienteDto(clienteDto);

                    wrapper.setFattura(fatturaDto);
                    elencoAutofatture.add(wrapper);
                }
                catch (Exception e)
                {
                    logger.error("Errore nella generazione XML dell'autofattura {}: {}", id, e.getMessage(), e);
                }
            }
            logger.info("{} autofatture pronte per l'invio SDI", elencoAutofatture.size());
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
    public void update(ExecutionContext arg0) throws ItemStreamException {}

    @Override
    public FatturaElettronicaWrapperDto read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException
    {
        if ( !elencoAutofatture.isEmpty() )
        {
            return elencoAutofatture.remove(0);
        }
        return null;
    }

}
