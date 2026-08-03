package it.tinna.smartdoc.server.service.riconciliazione.parser;

import java.io.IOException;
import java.util.List;

import it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto;

/**
 * Strategy per il parsing di un estratto conto in un formato specifico (MT940, CSV, ...).
 * Ogni implementazione produce una lista di movimenti "grezzi", indipendenti dal formato
 * sorgente, che vengono poi passati al motore di matching.
 */
public interface EstrattoContoParser
{

    List<MovimentoEstrattoContoDto> parse(byte[] fileContent) throws IOException;

}
