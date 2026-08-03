package it.tinna.smartdoc.server.service.riconciliazione;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import it.tinna.smartdoc.shared.dto.riconciliazione.MatchCandidatoDto;
import it.tinna.smartdoc.shared.dto.riconciliazione.MovimentoEstrattoContoDto;

/**
 * Motore di scoring per l'abbinamento tra un movimento bancario e le scadenze aperte
 * candidate. Non abbina mai "alla cieca": un abbinamento e' automatico solo se il
 * punteggio e' alto E il candidato e' l'unico sopra soglia (nessuna ambiguita').
 * In tutti gli altri casi i candidati vengono solo proposti per conferma manuale.
 */
public class RiconciliazioneMatchingService
{

    public static final BigDecimal SOGLIA_AUTO_MATCH = BigDecimal.valueOf(85);

    public static final BigDecimal SOGLIA_MARGINE_UNIVOCITA = BigDecimal.valueOf(10);

    public static final BigDecimal SOGLIA_CANDIDATO_MINIMO = BigDecimal.valueOf(40);

    private static final DateTimeFormatter DATA_IT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Calcola e ordina (per punteggio decrescente) i candidati per un movimento,
     * scartando quelli sotto la soglia minima o con importo incompatibile.
     */
    public List<MatchCandidatoDto> calcolaCandidati(MovimentoEstrattoContoDto movimento,
                                                     List<MatchCandidatoDto> candidatiPool)
    {
        List<MatchCandidatoDto> risultato = new ArrayList<>();
        for ( MatchCandidatoDto candidato : candidatiPool )
        {
            BigDecimal score = calcolaScore(movimento, candidato);
            if ( score.compareTo(SOGLIA_CANDIDATO_MINIMO) >= 0 )
            {
                candidato.setScore(score);
                risultato.add(candidato);
            }
        }
        risultato.sort(Comparator.comparing(MatchCandidatoDto::getScore).reversed());
        return risultato;
    }

    /**
     * Determina se il primo candidato (gia' ordinato per score) e' abbastanza affidabile
     * da poter essere confermato automaticamente, senza intervento umano.
     */
    public boolean isAutoMatchAffidabile(List<MatchCandidatoDto> candidatiOrdinati)
    {
        if ( candidatiOrdinati.isEmpty() )
        {
            return false;
        }
        BigDecimal primo = candidatiOrdinati.get(0).getScore();
        if ( primo.compareTo(SOGLIA_AUTO_MATCH) < 0 )
        {
            return false;
        }
        if ( candidatiOrdinati.size() == 1 )
        {
            return true;
        }
        BigDecimal secondo = candidatiOrdinati.get(1).getScore();
        // Se il secondo candidato e' troppo vicino al primo, l'abbinamento e' ambiguo:
        // meglio chiedere conferma manuale che sbagliare in automatico.
        return primo.subtract(secondo).compareTo(SOGLIA_MARGINE_UNIVOCITA) >= 0;
    }

    private BigDecimal calcolaScore(MovimentoEstrattoContoDto movimento,
                                    MatchCandidatoDto candidato)
    {
        BigDecimal importoMovimento = movimento.getImporto().abs();
        BigDecimal importoCandidato = candidato.getImporto() != null ? candidato.getImporto().abs() : BigDecimal.ZERO;
        BigDecimal diffImporto = importoMovimento.subtract(importoCandidato).abs();

        // Scarto immediato se l'importo e' troppo diverso (oltre 2% o 2 euro, il maggiore dei due)
        BigDecimal tolleranza = importoCandidato.multiply(BigDecimal.valueOf(0.02)).max(BigDecimal.valueOf(2));
        if ( diffImporto.compareTo(tolleranza) > 0 )
        {
            return BigDecimal.ZERO;
        }

        BigDecimal score = BigDecimal.ZERO;

        // Importo: fino a 60 punti
        if ( diffImporto.compareTo(BigDecimal.ZERO) == 0 )
        {
            score = score.add(BigDecimal.valueOf(60));
        }
        else
        {
            score = score.add(BigDecimal.valueOf(35));
        }

        // Prossimita' data: fino a 30 punti
        long giorniDistanza = giorniTraDataEStringa(movimento.getDataValuta(), candidato.getDataScadenza());
        if ( giorniDistanza == 0 )
        {
            score = score.add(BigDecimal.valueOf(30));
        }
        else if ( giorniDistanza <= 5 )
        {
            score = score.add(BigDecimal.valueOf(20));
        }
        else if ( giorniDistanza <= 15 )
        {
            score = score.add(BigDecimal.valueOf(10));
        }

        // Somiglianza testuale soggetto/causale: fino a 10 punti
        if ( nomeCompareInTesto(candidato.getSoggetto(), movimento.getCausaleBanca())
            || nomeCompareInTesto(candidato.getSoggetto(), movimento.getControparte()) )
        {
            score = score.add(BigDecimal.valueOf(10));
        }

        return score;
    }

    private long giorniTraDataEStringa(LocalDate dataMovimento,
                                       String dataScadenzaIt)
    {
        if ( dataMovimento == null || StringUtils.isBlank(dataScadenzaIt) )
        {
            return Long.MAX_VALUE;
        }
        try
        {
            LocalDate dataScadenza = LocalDate.parse(dataScadenzaIt, DATA_IT);
            return Math.abs(java.time.temporal.ChronoUnit.DAYS.between(dataMovimento, dataScadenza));
        }
        catch ( Exception e )
        {
            return Long.MAX_VALUE;
        }
    }

    private boolean nomeCompareInTesto(String soggetto,
                                       String testo)
    {
        if ( StringUtils.isBlank(soggetto) || StringUtils.isBlank(testo) )
        {
            return false;
        }
        // Confronta solo la prima "parola forte" del ragione sociale (es. "ROSSI" di "Rossi S.r.l.")
        // per tollerare abbreviazioni/forme societarie diverse tra banca e anagrafica.
        String primaParola = soggetto.trim().split("\\s+")[0].toLowerCase();
        if ( primaParola.length() < 3 )
        {
            return false;
        }
        return testo.toLowerCase().contains(primaParola);
    }

}
