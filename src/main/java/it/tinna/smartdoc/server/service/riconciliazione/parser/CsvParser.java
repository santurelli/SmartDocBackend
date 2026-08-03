package it.tinna.smartdoc.server.service.riconciliazione.parser;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.RiconciliazioneCsvMappingDto;

/**
 * Parser CSV configurabile: ogni banca esporta con colonne/ordine/formato data diversi,
 * quindi il mapping (indice colonna, delimitatore, formato data) viene passato dall'esterno
 * anziche' essere cablato qui. Il mapping viene salvato una volta per risorsa/banca
 * (vedi {@code d_e_riconciliazione_csv_mapping}) cosi' l'utente non lo ridigita ad ogni import.
 */
public class CsvParser implements EstrattoContoParser
{

    private final RiconciliazioneCsvMappingDto mapping;

    public CsvParser(RiconciliazioneCsvMappingDto mapping)
    {
        this.mapping = mapping;
    }

    @Override
    public List<MovimentoEstrattoContoDto> parse(byte[] fileContent) throws IOException
    {
        List<MovimentoEstrattoContoDto> movimenti = new ArrayList<>();
        String delimitatore = StringUtils.defaultIfBlank(mapping.getDelimitatore(), ";");
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
            StringUtils.defaultIfBlank(mapping.getFormatoData(), "dd/MM/yyyy"));
        boolean hasHeader = mapping.getFlHaIntestazione() == null || mapping.getFlHaIntestazione() == 1;
        boolean importoUnico = mapping.getFlImportoUnicoConSegno() == null || mapping.getFlImportoUnicoConSegno() == 1;

        try ( BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(fileContent), StandardCharsets.UTF_8)) )
        {
            String line;
            boolean firstLine = true;
            while ( (line = reader.readLine()) != null )
            {
                if ( StringUtils.isBlank(line) )
                {
                    continue;
                }
                if ( firstLine && hasHeader )
                {
                    firstLine = false;
                    continue;
                }
                firstLine = false;

                String[] cols = line.split(java.util.regex.Pattern.quote(delimitatore), -1);
                MovimentoEstrattoContoDto dto = new MovimentoEstrattoContoDto();

                LocalDate data = LocalDate.parse(col(cols, mapping.getColData()).trim(), dateFormatter);
                dto.setDataValuta(data);
                dto.setDataContabile(data);

                if ( importoUnico )
                {
                    dto.setImporto(parseImporto(col(cols, mapping.getColImporto())));
                }
                else
                {
                    BigDecimal entrata = parseImportoOrZero(col(cols, mapping.getColImportoEntrata()));
                    BigDecimal uscita = parseImportoOrZero(col(cols, mapping.getColImportoUscita()));
                    dto.setImporto(entrata.subtract(uscita));
                }

                dto.setCausaleBanca(col(cols, mapping.getColCausale()));
                dto.setControparte(col(cols, mapping.getColControparte()));
                dto.setIbanControparte(col(cols, mapping.getColIbanControparte()));

                movimenti.add(dto);
            }
        }

        return movimenti;
    }

    private String col(String[] cols, Integer index)
    {
        if ( index == null || index < 0 || index >= cols.length )
        {
            return "";
        }
        return cols[index].trim();
    }

    private BigDecimal parseImporto(String raw)
    {
        String cleaned = raw.replace(".", "").replace(',', '.').trim();
        return new BigDecimal(cleaned);
    }

    private BigDecimal parseImportoOrZero(String raw)
    {
        if ( StringUtils.isBlank(raw) )
        {
            return BigDecimal.ZERO;
        }
        return parseImporto(raw);
    }

}
