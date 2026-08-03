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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto;

/**
 * Parser per estratti conto in formato SWIFT MT940 (lo standard piu' diffuso tra le
 * banche italiane per l'export dei movimenti).
 *
 * Legge le coppie di tag :61: (riga movimento) + :86: (causale/informazioni aggiuntive)
 * presenti tra un tag :20:/:25: e il successivo saldo :62F:. Non copre l'intera specifica
 * MT940 (es. storni RC/RD, campi strutturati SEPA dentro :86:), ma il sottoinsieme che
 * copre la quasi totalita' degli estratti conto reali.
 */
public class Mt940Parser implements EstrattoContoParser
{

    private static final Pattern TAG_61 = Pattern.compile(
        "^(\\d{6})(\\d{4})?([CD])(\\d+,\\d*)"
    );

    private static final DateTimeFormatter YYMMDD = DateTimeFormatter.ofPattern("yyMMdd");

    @Override
    public List<MovimentoEstrattoContoDto> parse(byte[] fileContent) throws IOException
    {
        List<MovimentoEstrattoContoDto> movimenti = new ArrayList<>();

        try ( BufferedReader reader = new BufferedReader(
            new InputStreamReader(new ByteArrayInputStream(fileContent), StandardCharsets.ISO_8859_1)) )
        {
            String line;
            StringBuilder currentTagContent = null;
            String currentTagName = null;
            MovimentoEstrattoContoDto pendingMovimento = null;

            while ( (line = reader.readLine()) != null )
            {
                if ( line.startsWith(":") )
                {
                    // Chiude il tag precedente prima di aprirne uno nuovo
                    pendingMovimento = flushTag(movimenti, pendingMovimento, currentTagName, currentTagContent);

                    int endTag = line.indexOf(':', 1);
                    if ( endTag < 0 )
                    {
                        currentTagName = null;
                        currentTagContent = null;
                        continue;
                    }
                    currentTagName = line.substring(1, endTag);
                    currentTagContent = new StringBuilder(line.substring(endTag + 1));
                }
                else if ( currentTagContent != null )
                {
                    // Continuazione di un tag multilinea (tipico di :86:)
                    currentTagContent.append(' ').append(line);
                }
            }
            flushTag(movimenti, pendingMovimento, currentTagName, currentTagContent);
        }

        return movimenti;
    }

    private MovimentoEstrattoContoDto flushTag(List<MovimentoEstrattoContoDto> movimenti,
                                               MovimentoEstrattoContoDto pendingMovimento,
                                               String tagName,
                                               StringBuilder tagContent)
    {
        if ( tagName == null || tagContent == null )
        {
            return pendingMovimento;
        }

        if ( "61".equals(tagName) )
        {
            // Un nuovo :61: senza :86: precedente per il movimento pendente: lo emette comunque
            if ( pendingMovimento != null )
            {
                movimenti.add(pendingMovimento);
            }
            return parseMovimentoLine(tagContent.toString());
        }

        if ( "86".equals(tagName) && pendingMovimento != null )
        {
            pendingMovimento.setCausaleBanca(tagContent.toString().trim());
            movimenti.add(pendingMovimento);
            return null;
        }

        return pendingMovimento;
    }

    private MovimentoEstrattoContoDto parseMovimentoLine(String content)
    {
        Matcher m = TAG_61.matcher(content);
        if ( !m.find() )
        {
            return null;
        }

        MovimentoEstrattoContoDto dto = new MovimentoEstrattoContoDto();
        LocalDate dataValuta = LocalDate.parse(m.group(1), YYMMDD);
        dto.setDataValuta(dataValuta);
        dto.setDataContabile(dataValuta);

        boolean entrata = "C".equals(m.group(3));
        BigDecimal importo = new BigDecimal(m.group(4).replace(',', '.'));
        dto.setImporto(entrata ? importo : importo.negate());

        return dto;
    }

}
