package it.tinna.smartdoc.server.delegate.contabilita;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.contabilita.RegistrazioneContabileDao;
import it.tinna.smartdoc.server.dao.pianoconti.PianoDeiContiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.service.contabilita.ContoResolverService;
import it.tinna.smartdoc.server.util.IOUtility;
import it.tinna.smartdoc.server.util.ReportLoader;
import it.tinna.smartdoc.shared.dto.contabilita.LibroGiornaleRigaStampaDto;
import it.tinna.smartdoc.shared.dto.contabilita.MastrinoDto;
import it.tinna.smartdoc.shared.dto.contabilita.MastrinoRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.MovimentoContabileRigaDto;
import it.tinna.smartdoc.shared.dto.contabilita.RegistrazioneContabileDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import it.tinna.smartdoc.shared.dto.pianoconti.PianoContoDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoDto;
import it.tinna.smartdoc.shared.dto.documenti.NotaCreditoFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.documenti.TipoFattura;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Genera automaticamente la registrazione contabile in partita doppia per Fatture, Note di Debito e Note di Credito clienti
 * (ciclo attivo) e per le Fatture Fornitore (ciclo passivo), usando la cascata di risoluzione conti costruita in Fase 1/2
 * (articolo/sottocategoria/categoria/cliente/fornitore -> ruolo generico).
 *
 * Nota bene: la "Nota di Debito" NON e' un documento a se' nel dominio applicativo, e' un TipoFattura
 * memorizzato nella stessa tabella/DTO delle Fatture (vedi FatturaDto.getTipoFattura()) - contabilmente
 * si comporta come una Fattura (aumenta credito v/cliente, ricavo, IVA a debito), cambia solo l'etichetta.
 * La Fattura Proforma invece NON e' un documento fiscalmente rilevante: non genera alcuna scrittura.
 *
 * La Nota di Credito e' lo specchio contabile della Fattura: stessi conti, Dare/Avere invertiti
 * (riduce il credito verso il cliente, il ricavo e l'IVA a debito precedentemente registrati).
 *
 * La Fattura Fornitore e' lo specchio del ciclo attivo: costo (Dare) + IVA a credito (Dare) contro
 * debito v/fornitore (Avere), sui conti risolti da ContoResolverService.resolveContoCostoArticolo /
 * resolveContoFornitore invece dei corrispondenti conti ricavo/cliente.
 *
 * Righe senza articolo di catalogo (fuori magazzino, idProdotto nullo ma imponibile reale) finiscono
 * sul conto generico di ruolo (RICAVI_VENDITE/COSTI_ACQUISTI), esattamente come un articolo che non ha
 * un conto specifico mappato: non c'e' un articolo da far risalire nella cascata di risoluzione.
 *
 * Semplificazioni note di questa versione (Fase 3):
 * - Bollo e spese incasso non ancora mappati a un conto specifico, non entrano nella registrazione.
 * - L'IVA viene accorpata su un unico conto "IVA a debito"/"IVA a credito" indipendentemente dall'aliquota.
 */
@Transactional(readOnly = true)
@Service
public class RegistrazioneContabileDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ChiusuraEsercizioDelegate chiusuraEsercizioDelegate;

    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    @Transactional(rollbackFor = Throwable.class)
    public void generaDaFattura(FatturaDto dto) {
        if (dto.getTipoFattura() == TipoFattura.FATTURA_PROFORMA) {
            // Non e' un documento fiscale: nessuna scrittura contabile. Se in precedenza (per un cambio
            // tipo poco probabile ma non impossibile) ne esisteva una, la rimuoviamo per coerenza.
            try {
                new RegistrazioneContabileDao(jdbcTemplate).deleteByDocumento("FATTURA", dto.getId());
            } catch (Exception e) {
                _log.error("Errore nella pulizia della registrazione contabile per fattura proforma {}", dto.getId(), e);
            }
            return;
        }
        boolean notaDebito = dto.getTipoFattura() == TipoFattura.NOTA_DEBITO;
        String tipoDocumento = notaDebito ? "NOTA_DEBITO" : "FATTURA";
        String descrizioneTipo = notaDebito ? "Nota di Debito" : "Fattura";
        // Se un update ha cambiato il tipo documento (es. da Nota di Debito a Fattura), la registrazione
        // generata in precedenza con l'altra etichetta non verrebbe trovata da generaScrittura (che cerca
        // per tipoDocumento corrente) e resterebbe orfana: la puliamo esplicitamente per sicurezza.
        try {
            new RegistrazioneContabileDao(jdbcTemplate).deleteByDocumento(notaDebito ? "FATTURA" : "NOTA_DEBITO", dto.getId());
        } catch (Exception e) {
            _log.warn("Errore nella pulizia della registrazione contabile con tipo documento alternativo per {} {}", descrizioneTipo, dto.getId(), e);
        }
        generaScrittura(tipoDocumento, descrizioneTipo, dto.getId(), dto.getNumDocumento(), dto.getDataDocumento(),
                dto.getIdCliente(), dto.getProdotti(), dto.getUserCreated(), false);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void generaDaNotaCredito(NotaCreditoDto dto) {
        generaScrittura("NOTA_CREDITO", "Nota di Credito", dto.getId(), dto.getNumDocumento(), dto.getDataDocumento(),
                dto.getIdCliente(), dto.getProdotti(), dto.getUserCreated(), true);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void generaDaFatturaFornitore(FatturaFornitoreDto dto) {
        generaScritturaPassiva("FATTURA_FORNITORE", "Fattura Fornitore", dto.getId(), dto.getNumDocumento(), dto.getDataDocumento(),
                dto.getIdFornitore(), dto.getProdotti(), dto.getUserCreated(), false);
    }

    @Transactional(rollbackFor = Throwable.class)
    public void generaDaNotaCreditoFornitore(NotaCreditoFornitoreDto dto) {
        generaScritturaPassiva("NOTA_CREDITO_FORNITORE", "Nota di Credito Fornitore", dto.getId(), dto.getNumDocumento(), dto.getDataDocumento(),
                dto.getIdFornitore(), dto.getProdotti(), dto.getUserCreated(), true);
    }

    /**
     * @param inverti true per la Nota di Credito: scambia Dare/Avere rispetto alla Fattura sugli stessi conti.
     */
    private void generaScrittura(String tipoDocumento, String descrizioneTipo, long idDocumento, Integer numDocumento,
                                 String dataDocumento, Integer idCliente, List<ProdottoDocumentoDto> prodotti,
                                 Long userCreated, boolean inverti) {
        try {
            if (chiusuraEsercizioDelegate.isEsercizioChiuso(dataDocumento)) {
                _log.warn("Registrazione contabile non generata per {} {}: l'esercizio della data documento ({}) e' chiuso", descrizioneTipo, idDocumento, dataDocumento);
                return;
            }

            ContoResolverService resolver = new ContoResolverService(jdbcTemplate);
            RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);

            // Idempotente: se il documento e' stato modificato (update), la registrazione precedente
            // non e' piu' valida (importi/conti potrebbero essere cambiati) e va rigenerata da zero.
            dao.deleteByDocumento(tipoDocumento, idDocumento);

            Integer idContoCliente = resolver.resolveContoCliente(idCliente);
            if (idContoCliente == null) {
                _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per crediti v/clienti (impostare un piano dei conti con ruolo CREDITI_CLIENTI)", descrizioneTipo, idDocumento);
                return;
            }

            Map<Integer, BigDecimal> ricaviPerConto = new LinkedHashMap<>();
            BigDecimal totaleImposta = BigDecimal.ZERO;

            if (prodotti != null) {
                for (ProdottoDocumentoDto riga : prodotti) {
                    // Non si esclude piu' in base a idProdotto == null: le righe fuori magazzino non hanno un
                    // articolo di catalogo ma hanno un imponibile reale e vanno comunque registrate (vedi sotto).
                    // Le uniche righe da escludere sono quelle senza importo (note testuali per esigibilita'
                    // differita, split payment, ecc. - aggiunte solo in memoria per la stampa PDF, mai salvate).
                    if (riga.getPrezzoImponibile() == 0) {
                        continue;
                    }
                    BigDecimal imponibile = BigDecimal.valueOf(riga.getPrezzoImponibile()).setScale(2, RoundingMode.HALF_UP);
                    // L'override manuale sulla riga (impostato dall'utente in fase di compilazione documento)
                    // ha sempre la precedenza sulla cascata automatica: un articolo di catalogo la segue
                    // (articolo -> sottocategoria -> categoria -> ruolo), una riga fuori magazzino non ha un
                    // articolo da risolvere e va dritta sul conto di ruolo generico.
                    Integer idContoRicavo = riga.getIdContoOverride() != null ? riga.getIdContoOverride()
                            : riga.getIdProdotto() != null
                            ? resolver.resolveContoRicavoArticolo(riga.getIdProdotto())
                            : resolver.resolveContoRuolo("RICAVI_VENDITE");
                    if (idContoRicavo == null) {
                        _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per ricavi (articolo {})", descrizioneTipo, idDocumento, riga.getIdProdotto());
                        return;
                    }
                    ricaviPerConto.merge(idContoRicavo, imponibile, BigDecimal::add);

                    if (riga.getIdAliquotaIva() != null) {
                        BigDecimal percentuale = resolver.resolvePercentualeIva(riga.getIdAliquotaIva());
                        BigDecimal imposta = imponibile.multiply(percentuale).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        totaleImposta = totaleImposta.add(imposta);
                    }
                }
            }

            if (ricaviPerConto.isEmpty()) {
                _log.info("Registrazione contabile non generata per {} {}: nessuna riga articolo con imponibile valorizzato", descrizioneTipo, idDocumento);
                return;
            }

            Integer idContoIvaDebito = totaleImposta.compareTo(BigDecimal.ZERO) > 0 ? resolver.resolveContoRuolo("IVA_DEBITO") : null;
            if (totaleImposta.compareTo(BigDecimal.ZERO) > 0 && idContoIvaDebito == null) {
                _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per IVA a debito", descrizioneTipo, idDocumento);
                return;
            }

            BigDecimal totaleRicavi = ricaviPerConto.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totaleMovimento = totaleRicavi.add(totaleImposta);

            List<MovimentoContabileRigaDto> righe = new ArrayList<>();
            int progr = 1;

            String descrizioneDoc = descrizioneTipo + " " + numDocumento;
            String descrizioneCliente = inverti ? "Storno credito v/cliente - " + descrizioneDoc : "Credito v/cliente - " + descrizioneDoc;
            String descrizioneRicavo = inverti ? "Storno ricavo vendita - " + descrizioneDoc : "Ricavo vendita - " + descrizioneDoc;
            String descrizioneIva = inverti ? "Storno IVA a debito - " + descrizioneDoc : "IVA a debito - " + descrizioneDoc;

            MovimentoContabileRigaDto rigaCliente = new MovimentoContabileRigaDto();
            rigaCliente.setIdConto(idContoCliente);
            rigaCliente.setImportoDare(inverti ? BigDecimal.ZERO : totaleMovimento);
            rigaCliente.setImportoAvere(inverti ? totaleMovimento : BigDecimal.ZERO);
            rigaCliente.setDescrizione(descrizioneCliente);
            rigaCliente.setnProgr(progr++);
            righe.add(rigaCliente);

            for (Map.Entry<Integer, BigDecimal> entry : ricaviPerConto.entrySet()) {
                MovimentoContabileRigaDto rigaRicavo = new MovimentoContabileRigaDto();
                rigaRicavo.setIdConto(entry.getKey());
                rigaRicavo.setImportoDare(inverti ? entry.getValue() : BigDecimal.ZERO);
                rigaRicavo.setImportoAvere(inverti ? BigDecimal.ZERO : entry.getValue());
                rigaRicavo.setDescrizione(descrizioneRicavo);
                rigaRicavo.setnProgr(progr++);
                righe.add(rigaRicavo);
            }

            if (totaleImposta.compareTo(BigDecimal.ZERO) > 0) {
                MovimentoContabileRigaDto rigaIva = new MovimentoContabileRigaDto();
                rigaIva.setIdConto(idContoIvaDebito);
                rigaIva.setImportoDare(inverti ? totaleImposta : BigDecimal.ZERO);
                rigaIva.setImportoAvere(inverti ? BigDecimal.ZERO : totaleImposta);
                rigaIva.setDescrizione(descrizioneIva);
                rigaIva.setnProgr(progr++);
                righe.add(rigaIva);
            }

            RegistrazioneContabileDto registrazione = new RegistrazioneContabileDto();
            registrazione.setDataRegistrazione(convertiDataItalianaInIso(dataDocumento));
            registrazione.setDescrizione(descrizioneDoc);
            registrazione.setTipoDocumento(tipoDocumento);
            registrazione.setIdDocumento((int) idDocumento);
            registrazione.setNumeroDocumento(String.valueOf(numDocumento));
            registrazione.setTotaleDare(totaleMovimento);
            registrazione.setTotaleAvere(totaleMovimento);
            registrazione.setUserCreated(userCreated);

            long idRegistrazione = dao.insertTestata(registrazione);
            for (MovimentoContabileRigaDto riga : righe) {
                dao.insertRiga(idRegistrazione, riga);
            }
        } catch (Exception e) {
            // Non blocchiamo mai il salvataggio del documento per un problema nella generazione della scrittura contabile:
            // e' una funzionalita' additiva, il documento resta valido anche senza registrazione associata.
            _log.error("Errore nella generazione della registrazione contabile per {} {}", descrizioneTipo, idDocumento, e);
        }
    }

    /**
     * Specchio di generaScrittura per il ciclo passivo: costo (Dare) + IVA a credito (Dare) contro
     * debito v/fornitore (Avere). inverti e' gia' previsto per una futura Nota di Credito Fornitore.
     */
    private void generaScritturaPassiva(String tipoDocumento, String descrizioneTipo, long idDocumento, Integer numDocumento,
                                        String dataDocumento, long idFornitore, List<ProdottoDocumentoDto> prodotti,
                                        Long userCreated, boolean inverti) {
        try {
            if (chiusuraEsercizioDelegate.isEsercizioChiuso(dataDocumento)) {
                _log.warn("Registrazione contabile non generata per {} {}: l'esercizio della data documento ({}) e' chiuso", descrizioneTipo, idDocumento, dataDocumento);
                return;
            }

            ContoResolverService resolver = new ContoResolverService(jdbcTemplate);
            RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);

            dao.deleteByDocumento(tipoDocumento, idDocumento);

            Integer idContoFornitore = resolver.resolveContoFornitore(idFornitore);
            if (idContoFornitore == null) {
                _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per debiti v/fornitori (impostare un piano dei conti con ruolo DEBITI_FORNITORI)", descrizioneTipo, idDocumento);
                return;
            }

            Map<Integer, BigDecimal> costiPerConto = new LinkedHashMap<>();
            BigDecimal totaleImposta = BigDecimal.ZERO;

            if (prodotti != null) {
                for (ProdottoDocumentoDto riga : prodotti) {
                    if (riga.getPrezzoImponibile() == 0) {
                        continue;
                    }
                    BigDecimal imponibile = BigDecimal.valueOf(riga.getPrezzoImponibile()).setScale(2, RoundingMode.HALF_UP);
                    Integer idContoCosto = riga.getIdContoOverride() != null ? riga.getIdContoOverride()
                            : riga.getIdProdotto() != null
                            ? resolver.resolveContoCostoArticolo(riga.getIdProdotto())
                            : resolver.resolveContoRuolo("COSTI_ACQUISTI");
                    if (idContoCosto == null) {
                        _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per costi (articolo {})", descrizioneTipo, idDocumento, riga.getIdProdotto());
                        return;
                    }
                    costiPerConto.merge(idContoCosto, imponibile, BigDecimal::add);

                    if (riga.getIdAliquotaIva() != null) {
                        BigDecimal percentuale = resolver.resolvePercentualeIva(riga.getIdAliquotaIva());
                        BigDecimal imposta = imponibile.multiply(percentuale).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
                        totaleImposta = totaleImposta.add(imposta);
                    }
                }
            }

            if (costiPerConto.isEmpty()) {
                _log.info("Registrazione contabile non generata per {} {}: nessuna riga articolo con imponibile valorizzato", descrizioneTipo, idDocumento);
                return;
            }

            Integer idContoIvaCredito = totaleImposta.compareTo(BigDecimal.ZERO) > 0 ? resolver.resolveContoRuolo("IVA_CREDITO") : null;
            if (totaleImposta.compareTo(BigDecimal.ZERO) > 0 && idContoIvaCredito == null) {
                _log.warn("Registrazione contabile non generata per {} {}: nessun conto risolto per IVA a credito", descrizioneTipo, idDocumento);
                return;
            }

            BigDecimal totaleCosti = costiPerConto.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totaleMovimento = totaleCosti.add(totaleImposta);

            List<MovimentoContabileRigaDto> righe = new ArrayList<>();
            int progr = 1;

            String descrizioneDoc = descrizioneTipo + " " + numDocumento;
            String descrizioneFornitore = inverti ? "Storno debito v/fornitore - " + descrizioneDoc : "Debito v/fornitore - " + descrizioneDoc;
            String descrizioneCosto = inverti ? "Storno costo acquisto - " + descrizioneDoc : "Costo acquisto - " + descrizioneDoc;
            String descrizioneIva = inverti ? "Storno IVA a credito - " + descrizioneDoc : "IVA a credito - " + descrizioneDoc;

            for (Map.Entry<Integer, BigDecimal> entry : costiPerConto.entrySet()) {
                MovimentoContabileRigaDto rigaCosto = new MovimentoContabileRigaDto();
                rigaCosto.setIdConto(entry.getKey());
                rigaCosto.setImportoDare(inverti ? BigDecimal.ZERO : entry.getValue());
                rigaCosto.setImportoAvere(inverti ? entry.getValue() : BigDecimal.ZERO);
                rigaCosto.setDescrizione(descrizioneCosto);
                rigaCosto.setnProgr(progr++);
                righe.add(rigaCosto);
            }

            if (totaleImposta.compareTo(BigDecimal.ZERO) > 0) {
                MovimentoContabileRigaDto rigaIva = new MovimentoContabileRigaDto();
                rigaIva.setIdConto(idContoIvaCredito);
                rigaIva.setImportoDare(inverti ? BigDecimal.ZERO : totaleImposta);
                rigaIva.setImportoAvere(inverti ? totaleImposta : BigDecimal.ZERO);
                rigaIva.setDescrizione(descrizioneIva);
                rigaIva.setnProgr(progr++);
                righe.add(rigaIva);
            }

            MovimentoContabileRigaDto rigaFornitore = new MovimentoContabileRigaDto();
            rigaFornitore.setIdConto(idContoFornitore);
            rigaFornitore.setImportoDare(inverti ? totaleMovimento : BigDecimal.ZERO);
            rigaFornitore.setImportoAvere(inverti ? BigDecimal.ZERO : totaleMovimento);
            rigaFornitore.setDescrizione(descrizioneFornitore);
            rigaFornitore.setnProgr(progr++);
            righe.add(rigaFornitore);

            RegistrazioneContabileDto registrazione = new RegistrazioneContabileDto();
            registrazione.setDataRegistrazione(convertiDataItalianaInIso(dataDocumento));
            registrazione.setDescrizione(descrizioneDoc);
            registrazione.setTipoDocumento(tipoDocumento);
            registrazione.setIdDocumento((int) idDocumento);
            registrazione.setNumeroDocumento(String.valueOf(numDocumento));
            registrazione.setTotaleDare(totaleMovimento);
            registrazione.setTotaleAvere(totaleMovimento);
            registrazione.setUserCreated(userCreated);

            long idRegistrazione = dao.insertTestata(registrazione);
            for (MovimentoContabileRigaDto riga : righe) {
                dao.insertRiga(idRegistrazione, riga);
            }
        } catch (Exception e) {
            _log.error("Errore nella generazione della registrazione contabile per {} {}", descrizioneTipo, idDocumento, e);
        }
    }

    private String convertiDataItalianaInIso(String dataItaliana) {
        if (dataItaliana == null || !dataItaliana.contains("/")) {
            return dataItaliana;
        }
        String[] parts = dataItaliana.split("/");
        return parts.length == 3 ? parts[2] + "-" + parts[1] + "-" + parts[0] : dataItaliana;
    }

    /**
     * Da chiamare quando il documento sorgente viene cancellato: rimuove la registrazione contabile
     * associata, per non lasciare scritture orfane nel Libro Giornale.
     */
    @Transactional(rollbackFor = Throwable.class)
    public void eliminaRegistrazione(String tipoDocumento, long idDocumento) {
        try {
            RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
            dao.deleteByDocumento(tipoDocumento, idDocumento);
            // La Nota di Debito condivide la stessa tabella/id delle Fatture (e' solo un TipoFattura):
            // ripuliamo anche quell'etichetta per sicurezza, indipendentemente da quale fosse al momento della cancellazione.
            if ("FATTURA".equals(tipoDocumento)) {
                dao.deleteByDocumento("NOTA_DEBITO", idDocumento);
            }
        } catch (Exception e) {
            _log.error("Errore nell'eliminazione della registrazione contabile per {} {}", tipoDocumento, idDocumento, e);
        }
    }

    /**
     * Mastrino di un conto: movimenti in ordine cronologico con saldo progressivo (Dare - Avere cumulato).
     * Il saldo di apertura e' sempre zero: non e' ancora gestita l'apertura di un nuovo esercizio con
     * riporto dei saldi patrimoniali degli anni precedenti (limite noto, da affrontare insieme al Bilancio).
     */
    public MastrinoDto getMastrino(long idConto, String dataDa, String dataA) throws SQLException {
        PianoContoDto conto = new PianoDeiContiDao(jdbcTemplate).getById(idConto);
        if (conto == null) {
            return null;
        }

        RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
        List<MastrinoRigaDto> righe = dao.getMovimentiPerConto(idConto, dataDa, dataA);

        BigDecimal totaleDare = BigDecimal.ZERO;
        BigDecimal totaleAvere = BigDecimal.ZERO;
        BigDecimal saldo = BigDecimal.ZERO;
        for (MastrinoRigaDto riga : righe) {
            totaleDare = totaleDare.add(riga.getImportoDare());
            totaleAvere = totaleAvere.add(riga.getImportoAvere());
            saldo = saldo.add(riga.getImportoDare()).subtract(riga.getImportoAvere());
            riga.setSaldoProgressivo(saldo);
        }

        MastrinoDto mastrino = new MastrinoDto();
        mastrino.setIdConto(idConto);
        mastrino.setCodiceConto(conto.getCodice());
        mastrino.setDescrizioneConto(conto.getDescrizione());
        mastrino.setTipoConto(conto.getTipo());
        mastrino.setRighe(righe);
        mastrino.setTotaleDare(totaleDare);
        mastrino.setTotaleAvere(totaleAvere);
        mastrino.setSaldoFinale(saldo);
        return mastrino;
    }

    public List<RegistrazioneContabileDto> getList(String tipoDocumento, String search) throws SQLException {
        return getList(tipoDocumento, search, null, null);
    }

    public List<RegistrazioneContabileDto> getList(String tipoDocumento, String search, String dataDa, String dataA) throws SQLException {
        RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
        List<RegistrazioneContabileDto> list = dao.getList(tipoDocumento, search, dataDa, dataA);
        for (RegistrazioneContabileDto reg : list) {
            reg.setRighe(dao.getRighe(reg.getId()));
        }
        return list;
    }

    /**
     * Stampa PDF del mastrino di un conto (report_stampa/mastrino.jrxml).
     */
    public DocumentoWrapperDto esportaMastrinoPdf(long idConto, String dataDa, String dataA) {
        try {
            MastrinoDto mastrino = getMastrino(idConto, dataDa, dataA);
            if (mastrino == null) {
                DocumentoWrapperDto result = new DocumentoWrapperDto();
                result.setFlusso(new byte[0]);
                result.setNome("blank.pdf");
                return result;
            }
            DatiAziendaDto datiAzienda = datiaziendaDelegate.getDatiAzienda();

            Map<String, Object> params = new HashMap<>();
            params.put("datiazienda", datiAzienda);
            params.put("codiceConto", mastrino.getCodiceConto());
            params.put("descrizioneConto", mastrino.getDescrizioneConto());
            params.put("periodo", (dataDa == null && dataA == null) ? "Tutto il periodo"
                    : "Dal " + (dataDa == null ? "-" : dataDa) + " al " + (dataA == null ? "-" : dataA));
            params.put("totaleDare", mastrino.getTotaleDare());
            params.put("totaleAvere", mastrino.getTotaleAvere());
            params.put("saldoFinale", mastrino.getSaldoFinale());

            JasperReport report = ReportLoader.getReport("mastrino.jrxml");
            byte[] bytes = JasperRunManager.runReportToPdf(report, params, new JRBeanCollectionDataSource(mastrino.getRighe()));

            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome(IOUtility.getValidFilename("mastrino_" + mastrino.getCodiceConto()) + ".pdf");
            return result;
        } catch (Exception e) {
            _log.error("Errore nella generazione del pdf del mastrino {}", idConto, e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }

    /**
     * Stampa PDF del libro giornale (report_stampa/libro_giornale.jrxml): raggruppato per registrazione.
     */
    public DocumentoWrapperDto esportaLibroGiornalePdf(String tipoDocumento, String search, String dataDa, String dataA) {
        try {
            List<RegistrazioneContabileDto> registrazioni = getList(tipoDocumento, search, dataDa, dataA);
            List<LibroGiornaleRigaStampaDto> righeStampa = new ArrayList<>();
            for (RegistrazioneContabileDto reg : registrazioni) {
                for (MovimentoContabileRigaDto riga : reg.getRighe()) {
                    LibroGiornaleRigaStampaDto r = new LibroGiornaleRigaStampaDto();
                    r.setIdRegistrazione(reg.getId());
                    r.setDataRegistrazione(reg.getDataRegistrazione());
                    r.setDescrizioneRegistrazione(reg.getDescrizione());
                    r.setTipoDocumento(reg.getTipoDocumento());
                    r.setNumeroDocumento(reg.getNumeroDocumento());
                    r.setCodiceConto(riga.getCodiceConto());
                    r.setDescrizioneConto(riga.getDescrizioneConto());
                    r.setDescrizioneRiga(riga.getDescrizione());
                    r.setImportoDare(riga.getImportoDare());
                    r.setImportoAvere(riga.getImportoAvere());
                    righeStampa.add(r);
                }
            }
            DatiAziendaDto datiAzienda = datiaziendaDelegate.getDatiAzienda();

            Map<String, Object> params = new HashMap<>();
            params.put("datiazienda", datiAzienda);
            StringBuilder filtro = new StringBuilder();
            if (tipoDocumento != null && !tipoDocumento.isEmpty()) {
                filtro.append("Tipo documento: ").append(tipoDocumento);
            }
            if (search != null && !search.isEmpty()) {
                if (filtro.length() > 0) {
                    filtro.append(" - ");
                }
                filtro.append("Ricerca: \"").append(search).append("\"");
            }
            if (dataDa != null || dataA != null) {
                if (filtro.length() > 0) {
                    filtro.append(" - ");
                }
                filtro.append("Dal ").append(dataDa == null ? "-" : dataDa).append(" al ").append(dataA == null ? "-" : dataA);
            }
            params.put("filtro", filtro.length() > 0 ? filtro.toString() : "Tutte le registrazioni");

            JasperReport report = ReportLoader.getReport("libro_giornale.jrxml");
            byte[] bytes = JasperRunManager.runReportToPdf(report, params, new JRBeanCollectionDataSource(righeStampa));

            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome("libro_giornale.pdf");
            return result;
        } catch (Exception e) {
            _log.error("Errore nella generazione del pdf del libro giornale", e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }

    /**
     * Scritture manuali (giroconti, rettifiche, saldi di apertura da un vecchio gestionale, ecc.), a
     * differenza del resto del motore NON sono "mai bloccanti": qui e' l'utente stesso la fonte, quindi
     * si valida sul serio (Dare == Avere, almeno 2 righe, esercizio non chiuso) e si restituisce un
     * errore esplicito se qualcosa non torna, invece di salvare silenziosamente una scrittura sbagliata.
     */
    @Transactional(rollbackFor = Throwable.class)
    public long inserisciManuale(RegistrazioneContabileDto dto) throws SQLException {
        validaScritturaManuale(dto);

        RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
        dto.setTipoDocumento("MANUALE");
        dto.setIdDocumento(0);
        BigDecimal totale = totaleRighe(dto.getRighe(), true);
        dto.setTotaleDare(totale);
        dto.setTotaleAvere(totale);

        long id = dao.insertTestata(dto);
        int progr = 1;
        for (MovimentoContabileRigaDto riga : dto.getRighe()) {
            riga.setnProgr(progr++);
            dao.insertRiga(id, riga);
        }
        return id;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void aggiornaManuale(long id, RegistrazioneContabileDto dto) throws SQLException {
        validaScritturaManuale(dto);

        RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
        RegistrazioneContabileDto esistente = dao.getById(id);
        if (esistente == null || !"MANUALE".equals(esistente.getTipoDocumento())) {
            throw new SQLException("Scrittura manuale " + id + " non trovata (o non e' una scrittura manuale modificabile da qui)");
        }

        BigDecimal totale = totaleRighe(dto.getRighe(), true);
        dto.setTotaleDare(totale);
        dto.setTotaleAvere(totale);
        dao.updateTestata(id, dto);
        dao.deleteRighe(id);
        int progr = 1;
        for (MovimentoContabileRigaDto riga : dto.getRighe()) {
            riga.setnProgr(progr++);
            dao.insertRiga(id, riga);
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void eliminaManuale(long id) throws SQLException {
        RegistrazioneContabileDao dao = new RegistrazioneContabileDao(jdbcTemplate);
        RegistrazioneContabileDto esistente = dao.getById(id);
        if (esistente == null || !"MANUALE".equals(esistente.getTipoDocumento())) {
            throw new SQLException("Scrittura manuale " + id + " non trovata (o non e' una scrittura manuale eliminabile da qui)");
        }
        dao.softDelete(id);
    }

    private void validaScritturaManuale(RegistrazioneContabileDto dto) throws SQLException {
        if (dto.getDataRegistrazione() == null) {
            throw new SQLException("La data della scrittura e' obbligatoria");
        }
        if (dto.getRighe() == null || dto.getRighe().size() < 2) {
            throw new SQLException("Una scrittura contabile deve avere almeno due righe");
        }
        for (MovimentoContabileRigaDto riga : dto.getRighe()) {
            if (riga.getIdConto() == null) {
                throw new SQLException("Ogni riga deve specificare un conto");
            }
            BigDecimal dare = riga.getImportoDare() != null ? riga.getImportoDare() : BigDecimal.ZERO;
            BigDecimal avere = riga.getImportoAvere() != null ? riga.getImportoAvere() : BigDecimal.ZERO;
            if (dare.compareTo(BigDecimal.ZERO) > 0 && avere.compareTo(BigDecimal.ZERO) > 0) {
                throw new SQLException("Una riga non puo' avere sia un importo in Dare che uno in Avere");
            }
            if (dare.compareTo(BigDecimal.ZERO) == 0 && avere.compareTo(BigDecimal.ZERO) == 0) {
                throw new SQLException("Ogni riga deve avere un importo in Dare o in Avere");
            }
        }
        BigDecimal totaleDare = totaleRighe(dto.getRighe(), true);
        BigDecimal totaleAvere = totaleRighe(dto.getRighe(), false);
        if (totaleDare.compareTo(totaleAvere) != 0) {
            throw new SQLException("La scrittura non e' bilanciata: Dare " + totaleDare + " diverso da Avere " + totaleAvere);
        }
        if (isEsercizioChiusoPubblico(dto.getDataRegistrazione())) {
            throw new SQLException("Non e' possibile registrare una scrittura nell'esercizio " + estraiAnnoIso(dto.getDataRegistrazione()) + ": e' gia' chiuso");
        }
    }

    private BigDecimal totaleRighe(List<MovimentoContabileRigaDto> righe, boolean dare) {
        BigDecimal totale = BigDecimal.ZERO;
        for (MovimentoContabileRigaDto riga : righe) {
            BigDecimal importo = dare ? riga.getImportoDare() : riga.getImportoAvere();
            totale = totale.add(importo != null ? importo : BigDecimal.ZERO);
        }
        return totale;
    }

    private boolean isEsercizioChiusoPubblico(String dataIso) throws SQLException {
        // La data qui e' gia' in formato ISO (yyyy-MM-dd), mentre ChiusuraEsercizioDelegate si aspetta
        // il formato italiano usato dai documenti: converto solo per riusare la stessa logica di controllo.
        if (dataIso == null || !dataIso.contains("-")) {
            return false;
        }
        String[] parts = dataIso.split("-");
        if (parts.length != 3) {
            return false;
        }
        String dataItaliana = parts[2] + "/" + parts[1] + "/" + parts[0];
        return chiusuraEsercizioDelegate.isEsercizioChiuso(dataItaliana);
    }

    private String estraiAnnoIso(String dataIso) {
        return dataIso != null && dataIso.contains("-") ? dataIso.split("-")[0] : dataIso;
    }
}
