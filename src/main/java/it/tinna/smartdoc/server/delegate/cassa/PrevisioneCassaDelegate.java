package it.tinna.smartdoc.server.delegate.cassa;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.tinna.smartdoc.server.dao.cassa.PrevisioneCassaDao;
import it.tinna.smartdoc.server.dao.cassa.PrevisioneCassaDao.MovimentoScadenza;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.cassa.PrevisioneCassaContoDto;
import it.tinna.smartdoc.shared.dto.cassa.PrevisioneCassaDto;
import it.tinna.smartdoc.shared.dto.cassa.PuntoSerieCassaDto;
import it.tinna.smartdoc.shared.dto.cassa.ScadenzaScadutaDto;

/**
 * Previsione di cassa: saldo attuale per conto (saldo iniziale + scadenze gia' saldate) e
 * proiezione a 30/60/90 giorni sommando le sole scadenze future NON ancora saldate.
 * Le scadenze scadute e non saldate sono deliberatamente escluse dalla proiezione (approccio
 * prudente confermato con l'utente): sono un rischio, non una certezza, e vengono riportate
 * a parte in un elenco separato.
 */
@Service
public class PrevisioneCassaDelegate extends BaseDelegate {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int GIORNI_SERIE = 90;

    public PrevisioneCassaDto calcola() throws SQLException {
        PrevisioneCassaDao dao = new PrevisioneCassaDao(jdbcTemplate);

        List<PrevisioneCassaContoDto> conti = dao.getContiConSaldoIniziale();
        Map<Integer, PrevisioneCassaContoDto> contiPerId = new HashMap<>();
        for (PrevisioneCassaContoDto conto : conti) {
            conto.setSaldoAttuale(conto.getSaldoIniziale());
            conto.setSaldoPrevisto30(conto.getSaldoIniziale());
            conto.setSaldoPrevisto60(conto.getSaldoIniziale());
            conto.setSaldoPrevisto90(conto.getSaldoIniziale());
            contiPerId.put(conto.getIdRisorsa(), conto);
        }

        List<MovimentoScadenza> movimenti = dao.getMovimenti();

        LocalDate oggi = LocalDate.now();
        LocalDate limite30 = oggi.plusDays(30);
        LocalDate limite60 = oggi.plusDays(60);
        LocalDate limite90 = oggi.plusDays(GIORNI_SERIE);

        BigDecimal saldoAttualeTotale = somma(conti, PrevisioneCassaContoDto::getSaldoIniziale);
        BigDecimal saldoPrevisto30 = saldoAttualeTotale;
        BigDecimal saldoPrevisto60 = saldoAttualeTotale;
        BigDecimal saldoPrevisto90 = saldoAttualeTotale;

        // Serie giornaliera cumulata per il grafico, aggregata su tutti i conti.
        Map<LocalDate, BigDecimal> flussoPerGiorno = new HashMap<>();

        List<ScadenzaScadutaDto> scadute = new ArrayList<>();
        BigDecimal totaleScadutoDaIncassare = BigDecimal.ZERO;
        BigDecimal totaleScadutoDaPagare = BigDecimal.ZERO;

        for (MovimentoScadenza m : movimenti) {
            LocalDate dataScadenza;
            try {
                dataScadenza = LocalDate.parse(m.data, FORMATO_DATA);
            } catch (Exception e) {
                continue; // data non valida/assente, riga ignorata
            }

            if (m.saldato) {
                // Gia' avvenuto: contribuisce al saldo attuale (e quindi anche alle proiezioni,
                // che partono dal saldo attuale).
                saldoAttualeTotale = saldoAttualeTotale.add(m.importo);
                saldoPrevisto30 = saldoPrevisto30.add(m.importo);
                saldoPrevisto60 = saldoPrevisto60.add(m.importo);
                saldoPrevisto90 = saldoPrevisto90.add(m.importo);
                PrevisioneCassaContoDto conto = contiPerId.get(m.idRisorsa);
                if (conto != null) {
                    conto.setSaldoAttuale(conto.getSaldoAttuale().add(m.importo));
                    conto.setSaldoPrevisto30(conto.getSaldoPrevisto30().add(m.importo));
                    conto.setSaldoPrevisto60(conto.getSaldoPrevisto60().add(m.importo));
                    conto.setSaldoPrevisto90(conto.getSaldoPrevisto90().add(m.importo));
                }
                continue;
            }

            // Non saldato:
            if (dataScadenza.isBefore(oggi)) {
                // Scaduto: NON entra nella proiezione, va nell'elenco a rischio.
                ScadenzaScadutaDto s = new ScadenzaScadutaDto();
                s.setTipo(m.tipo);
                s.setTipoDocumento(m.tipoDocumento);
                s.setNumeroDocumento(m.numeroDocumento);
                s.setSoggetto(m.soggetto);
                s.setDataScadenza(m.data);
                s.setImporto(m.importo.abs());
                s.setGiorniRitardo((int) java.time.temporal.ChronoUnit.DAYS.between(dataScadenza, oggi));
                scadute.add(s);
                if ("INCASSO".equals(m.tipo)) {
                    totaleScadutoDaIncassare = totaleScadutoDaIncassare.add(m.importo.abs());
                } else {
                    totaleScadutoDaPagare = totaleScadutoDaPagare.add(m.importo.abs());
                }
                continue;
            }

            // Futuro, non saldato: entra nella proiezione entro i rispettivi orizzonti.
            if (!dataScadenza.isAfter(limite30)) {
                saldoPrevisto30 = saldoPrevisto30.add(m.importo);
                PrevisioneCassaContoDto conto = contiPerId.get(m.idRisorsa);
                if (conto != null) conto.setSaldoPrevisto30(conto.getSaldoPrevisto30().add(m.importo));
            }
            if (!dataScadenza.isAfter(limite60)) {
                saldoPrevisto60 = saldoPrevisto60.add(m.importo);
                PrevisioneCassaContoDto conto = contiPerId.get(m.idRisorsa);
                if (conto != null) conto.setSaldoPrevisto60(conto.getSaldoPrevisto60().add(m.importo));
            }
            if (!dataScadenza.isAfter(limite90)) {
                saldoPrevisto90 = saldoPrevisto90.add(m.importo);
                PrevisioneCassaContoDto conto = contiPerId.get(m.idRisorsa);
                if (conto != null) conto.setSaldoPrevisto90(conto.getSaldoPrevisto90().add(m.importo));

                flussoPerGiorno.merge(dataScadenza, m.importo, BigDecimal::add);
            }
        }

        List<PuntoSerieCassaDto> serie = costruisciSerie(saldoAttualeTotale, flussoPerGiorno, oggi);

        scadute.sort(Comparator.comparing(ScadenzaScadutaDto::getGiorniRitardo).reversed());

        PrevisioneCassaDto risultato = new PrevisioneCassaDto();
        risultato.setSaldoAttualeTotale(arrotonda(saldoAttualeTotale));
        risultato.setSaldoPrevisto30(arrotonda(saldoPrevisto30));
        risultato.setSaldoPrevisto60(arrotonda(saldoPrevisto60));
        risultato.setSaldoPrevisto90(arrotonda(saldoPrevisto90));
        risultato.setSerieGiornaliera(serie);
        risultato.setPerConto(arrotondaConti(conti));
        risultato.setScadute(scadute);
        risultato.setTotaleScadutoDaIncassare(arrotonda(totaleScadutoDaIncassare));
        risultato.setTotaleScadutoDaPagare(arrotonda(totaleScadutoDaPagare));
        return risultato;
    }

    private List<PuntoSerieCassaDto> costruisciSerie(BigDecimal saldoAttuale, Map<LocalDate, BigDecimal> flussoPerGiorno, LocalDate oggi) {
        List<PuntoSerieCassaDto> serie = new ArrayList<>();
        BigDecimal cumulato = saldoAttuale;
        for (int i = 0; i <= GIORNI_SERIE; i++) {
            LocalDate giorno = oggi.plusDays(i);
            BigDecimal flusso = flussoPerGiorno.get(giorno);
            if (flusso != null) {
                cumulato = cumulato.add(flusso);
            }
            PuntoSerieCassaDto punto = new PuntoSerieCassaDto();
            punto.setData(giorno.format(FORMATO_DATA));
            punto.setSaldo(arrotonda(cumulato));
            serie.add(punto);
        }
        return serie;
    }

    private List<PrevisioneCassaContoDto> arrotondaConti(List<PrevisioneCassaContoDto> conti) {
        for (PrevisioneCassaContoDto conto : conti) {
            conto.setSaldoAttuale(arrotonda(conto.getSaldoAttuale()));
            conto.setSaldoPrevisto30(arrotonda(conto.getSaldoPrevisto30()));
            conto.setSaldoPrevisto60(arrotonda(conto.getSaldoPrevisto60()));
            conto.setSaldoPrevisto90(arrotonda(conto.getSaldoPrevisto90()));
        }
        return conti;
    }

    private BigDecimal arrotonda(BigDecimal valore) {
        return valore.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal somma(List<PrevisioneCassaContoDto> conti, java.util.function.Function<PrevisioneCassaContoDto, BigDecimal> estrattore) {
        BigDecimal totale = BigDecimal.ZERO;
        for (PrevisioneCassaContoDto conto : conti) {
            totale = totale.add(estrattore.apply(conto));
        }
        return totale;
    }
}
