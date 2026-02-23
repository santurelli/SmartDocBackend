package it.tinna.smartdoc.batch.processors.inviofatturelettroniche;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import it.tinna.smartdoc.server.delegate.documenti.FatturaElettronicaDelegate;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaElettronicaWrapperDto;

public class GeneraNomeFileFatturaElettronicaProcessor implements ItemProcessor<FatturaElettronicaWrapperDto, FatturaElettronicaWrapperDto>
{

    private Logger                     logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FatturaElettronicaDelegate fatturaelettronicaDelegate;

    @Override
    public FatturaElettronicaWrapperDto process(FatturaElettronicaWrapperDto arg0) throws Exception
    {
        // il nome file fattura elettronica viene impostato solo per gli xml che non hanno avuto errori di validazione
        if ( StringUtils.isBlank(arg0.getFattura().getErroreValidazioneXml()) )
        {
            String progressivoFile = fatturaelettronicaDelegate.getProgressivoUnivocoFile();
            StringBuilder strB = new StringBuilder("IT02254010644_").append(progressivoFile).append(".xml");
            if ( arg0.getFattura() instanceof FatturaDto )
            {
                logger.info("Generato nome {} per la fattura con id {}", strB.toString(), arg0.getFattura().getId());
            }
            else
            {
                logger.info("Generato nome {} per la nota credito con id {}", strB.toString(), arg0.getFattura().getId());
            }
            arg0.setProgressivoFile(progressivoFile);
            arg0.setNomeFileFattura(strB.toString());
        }
        return arg0;
    }

}

