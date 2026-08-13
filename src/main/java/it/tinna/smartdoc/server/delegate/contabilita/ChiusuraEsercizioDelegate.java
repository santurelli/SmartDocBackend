package it.tinna.smartdoc.server.delegate.contabilita;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.contabilita.EsercizioDao;
import it.tinna.smartdoc.server.dao.contabilita.RegistrazioneContabileDao;
import it.tinna.smartdoc.server.dao.pianoconti.PianoDeiContiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.service.contabilita.ContoResolverService;
import it.tinna.smartdoc.shared.dto.contabilita.ChiusuraEsercizioPreviewDto;
import it.tinna.smartdoc.shared.dto.contabilita.ChiusuraEsercizioRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.MovimentoContabileRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.RegistrazioneContabileDto;
import it.tinna.smartdoc.shared.dto.pianoconti.PianoContoDto;

/**
 * Chiusura/apertura di un esercizio contabile (Fase 4).
 *
 * Approccio scelto (ibrido, quello che un commercialista si aspetta di vedere):
 * - Conti economici (COSTO/RICAVO): vengono azzerati con una scrittura di chiusura VERA, datata 31/12,
 *   verso il conto "Utile (perdita) d'esercizio" (ruolo UTILE_ESERCIZIO) - visibile nel Libro Giornale,
 *   cosi' si vede come si e' arrivati al risultato d'esercizio.
 * - Conti patrimoniali (ATTIVITA/PASSIVITA/PATRIMONIO_NETTO/IVA): NON vengono azzerati con scritture -
 *   il loro saldo di chiusura viene semplicemente salvato come saldo di apertura dell'anno successivo
 *   (tabella d_e_saldi_apertura). E' il comportamento corretto: un debito o un credito non "sparisce"
 *   a fine anno solo perche' cambia l'esercizio.
 *
 * Il blocco su un esercizio chiuso e' "soft" (solo warning nei log), coerente con tutto il resto del
 * motore di generazione automatica: vedi RegistrazioneContabileDelegate.isEsercizioChiuso().
 */
@Transactional(readOnly = true)
@Service
public class ChiusuraEsercizioDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public ChiusuraEsercizioPreviewDto anteprima(int anno) throws SQLException {
        return calcola(anno);
    }

    @Transactional(rollbackFor = Throwable.class)
    public ChiusuraEsercizioPreviewDto chiudi(int anno, Long userId) throws SQLException {
        EsercizioDao esDao = new EsercizioDao(jdbcTemplate);
        it.tinna.smartdoc.shared.dto.contabilita.EsercizioDto esistente = esDao.getEsercizio(anno);
        if (esistente != null && "CHIUSO".equals(esistente.getStato())) {
            throw new SQLException("L'esercizio " + anno + " e' gia' chiuso");
        }

        ChiusuraEsercizioPreviewDto preview = calcola(anno);

        Integer idContoUtile = null;
        if (!preview.getRigheEconomiche().isEmpty()) {
            idContoUtile = new ContoResolverService(jdbcTemplate).resolveContoRuolo("UTILE_ESERCIZIO");
            if (idContoUtile == null) {
                throw new SQLException("Nessun conto con ruolo UTILE_ESERCIZIO nel piano dei conti: impossibile chiudere l'esercizio");
            }

            RegistrazioneContabileDao regDao = new RegistrazioneContabileDao(jdbcTemplate);
            // Idempotente: se questo esercizio era stato chiuso e poi (in casi eccezionali) riaperto e richiuso,
            // rimpiazziamo la scrittura di chiusura precedente invece di duplicarla.
            regDao.deleteByDocumento("CHIUSURA_ESERCIZIO", anno);

            List<MovimentoContabileRigaDto> righe = new ArrayList<>();
            int progr = 1;
            for (ChiusuraEsercizioRigaDto r : preview.getRigheEconomiche()) {
                MovimentoContabileRigaDto riga = new MovimentoContabileRigaDto();
                riga.setIdConto(r.getIdConto().intValue());
                // Saldo (Dare - Avere) positivo, tipico di un COSTO: per azzerarlo lo giro in Avere.
                // Saldo negativo, tipico di un RICAVO: per azzerarlo lo giro in Dare.
                if (r.getSaldo().compareTo(BigDecimal.ZERO) >= 0) {
                    riga.setImportoAvere(r.getSaldo());
                    riga.setImportoDare(BigDecimal.ZERO);
                } else {
                    riga.setImportoDare(r.getSaldo().abs());
                    riga.setImportoAvere(BigDecimal.ZERO);
                }
                riga.setDescrizione("Chiusura " + r.getCodiceConto() + " - " + r.getDescrizioneConto());
                riga.setnProgr(progr++);
                righe.add(riga);
            }

            MovimentoContabileRigaDto rigaUtile = new MovimentoContabileRigaDto();
            rigaUtile.setIdConto(idContoUtile);
            BigDecimal utile = preview.getUtilePerdita();
            if (utile.compareTo(BigDecimal.ZERO) >= 0) {
                rigaUtile.setImportoAvere(utile);
                rigaUtile.setImportoDare(BigDecimal.ZERO);
            } else {
                rigaUtile.setImportoDare(utile.abs());
                rigaUtile.setImportoAvere(BigDecimal.ZERO);
            }
            rigaUtile.setDescrizione("Utile (perdita) d'esercizio " + anno);
            rigaUtile.setnProgr(progr);
            righe.add(rigaUtile);

            BigDecimal totale = righe.stream().map(MovimentoContabileRigaDto::getImportoDare).reduce(BigDecimal.ZERO, BigDecimal::add);

            RegistrazioneContabileDto registrazione = new RegistrazioneContabileDto();
            registrazione.setDataRegistrazione(anno + "-12-31");
            registrazione.setDescrizione("Chiusura esercizio " + anno);
            registrazione.setTipoDocumento("CHIUSURA_ESERCIZIO");
            registrazione.setIdDocumento(anno);
            registrazione.setNumeroDocumento(String.valueOf(anno));
            registrazione.setTotaleDare(totale);
            registrazione.setTotaleAvere(totale);
            registrazione.setUserCreated(userId);

            long idRegistrazione = regDao.insertTestata(registrazione);
            for (MovimentoContabileRigaDto riga : righe) {
                regDao.insertRiga(idRegistrazione, riga);
            }
        }

        boolean utileGiaRiportato = false;
        for (ChiusuraEsercizioRigaDto r : preview.getRighePatrimoniali()) {
            BigDecimal saldoRiportato = r.getSaldo();
            if (idContoUtile != null && r.getIdConto() == idContoUtile.longValue()) {
                // Un utile si registra in Avere (aumenta il saldo Avere, cioe' RIDUCE saldo = Dare - Avere);
                // una perdita in Dare (lo aumenta). Va quindi sottratto, non sommato, per restare coerenti
                // con la convenzione Dare-Avere usata su tutti gli altri conti (compresi gli altri Patrimonio
                // Netto/Passivita', che hanno normalmente saldo negativo).
                saldoRiportato = saldoRiportato.subtract(preview.getUtilePerdita());
                utileGiaRiportato = true;
            }
            esDao.upsertSaldoApertura(r.getIdConto(), anno + 1, saldoRiportato);
        }
        // Il conto Utile potrebbe non comparire tra i patrimoniali (nessun saldo di apertura ne' movimenti
        // pregressi su di esso), ma se quest'anno e' stato generato un utile/perdita va comunque riportato.
        if (idContoUtile != null && !utileGiaRiportato && preview.getUtilePerdita().compareTo(BigDecimal.ZERO) != 0) {
            BigDecimal saldoPrecedente = esDao.getSaldoApertura(idContoUtile, anno);
            esDao.upsertSaldoApertura(idContoUtile, anno + 1, saldoPrecedente.subtract(preview.getUtilePerdita()));
        }

        esDao.assicuraEsercizioAperto(anno + 1);
        esDao.chiudiEsercizio(anno, userId);

        return preview;
    }

    /**
     * true se l'esercizio a cui appartiene la data (formato italiano gg/mm/aaaa) risulta chiuso.
     * Usato da RegistrazioneContabileDelegate per decidere se generare o meno una scrittura.
     */
    public boolean isEsercizioChiuso(String dataItaliana) throws SQLException {
        Integer anno = estraiAnno(dataItaliana);
        if (anno == null) {
            return false;
        }
        it.tinna.smartdoc.shared.dto.contabilita.EsercizioDto esercizio = new EsercizioDao(jdbcTemplate).getEsercizio(anno);
        return esercizio != null && "CHIUSO".equals(esercizio.getStato());
    }

    private Integer estraiAnno(String dataItaliana) {
        if (dataItaliana == null || !dataItaliana.contains("/")) {
            return null;
        }
        String[] parts = dataItaliana.split("/");
        if (parts.length != 3) {
            return null;
        }
        try {
            return Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private ChiusuraEsercizioPreviewDto calcola(int anno) throws SQLException {
        PianoDeiContiDao pianoDao = new PianoDeiContiDao(jdbcTemplate);
        EsercizioDao esDao = new EsercizioDao(jdbcTemplate);
        List<PianoContoDto> conti = pianoDao.getList("");
        Map<Long, BigDecimal> movimentiAnno = esDao.getSaldiMovimentiAnno(anno);

        List<ChiusuraEsercizioRigaDto> economiche = new ArrayList<>();
        List<ChiusuraEsercizioRigaDto> patrimoniali = new ArrayList<>();
        BigDecimal utilePerdita = BigDecimal.ZERO;

        for (PianoContoDto conto : conti) {
            boolean economico = "COSTO".equals(conto.getTipo()) || "RICAVO".equals(conto.getTipo());
            BigDecimal saldoApertura = economico ? BigDecimal.ZERO : esDao.getSaldoApertura(conto.getId(), anno);
            BigDecimal movimenti = movimentiAnno.getOrDefault(conto.getId(), BigDecimal.ZERO);
            BigDecimal saldo = saldoApertura.add(movimenti);
            if (saldo.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            ChiusuraEsercizioRigaDto riga = new ChiusuraEsercizioRigaDto();
            riga.setIdConto(conto.getId());
            riga.setCodiceConto(conto.getCodice());
            riga.setDescrizioneConto(conto.getDescrizione());
            riga.setTipoConto(conto.getTipo());
            riga.setSaldo(saldo);

            if (economico) {
                economiche.add(riga);
                // Saldo Dare positivo (COSTO) riduce l'utile; saldo Avere/negativo (RICAVO) lo aumenta.
                utilePerdita = utilePerdita.subtract(saldo);
            } else {
                patrimoniali.add(riga);
            }
        }

        ChiusuraEsercizioPreviewDto preview = new ChiusuraEsercizioPreviewDto();
        preview.setAnno(anno);
        it.tinna.smartdoc.shared.dto.contabilita.EsercizioDto esistente = esDao.getEsercizio(anno);
        preview.setGiaChiuso(esistente != null && "CHIUSO".equals(esistente.getStato()));
        preview.setUtilePerdita(utilePerdita);
        preview.setRigheEconomiche(economiche);
        preview.setRighePatrimoniali(patrimoniali);
        return preview;
    }
}
