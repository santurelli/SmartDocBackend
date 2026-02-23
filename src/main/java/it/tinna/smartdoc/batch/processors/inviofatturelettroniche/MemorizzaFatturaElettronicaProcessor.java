package it.tinna.smartdoc.batch.processors.inviofatturelettroniche;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;
import lombok.Setter;

public class MemorizzaFatturaElettronicaProcessor implements ItemProcessor<FatturaElettronicaWrapperDto, FatturaElettronicaWrapperDto>
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Setter
    private String                     dbKey;

    @Override
    public FatturaElettronicaWrapperDto process(FatturaElettronicaWrapperDto arg0) throws Exception
    {
        // questo processor memorizza nella tabella d_e_fattura_elettroniche del db service la fattura elettronica creata (il pacchetto di appartenenza e il progressivo file verranno inseriti dal writer solo se la fattura è corretta)
        long idFatturaElettronica = fatturaelettronicaDelegate.memorizzaFatturaElettronica(dbKey, arg0);
        logger.info("Salvata fattura elettronica nel service db con id {}", idFatturaElettronica);
        arg0.setIdFatturaElettronica(idFatturaElettronica);
        return arg0;
    }

}

