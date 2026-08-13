package it.tinna.smartdoc.server.delegate.pianoconti;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.pianoconti.PianoDeiContiDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.pianoconti.PianoContoDto;

@Transactional(readOnly = true)
@Service
public class PianoDeiContiDelegate extends BaseDelegate {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Set standard di conti proposto come punto di partenza (personalizzabile dal tenant), valido per
     * i settori non specializzati (o quando il settore non e' stato impostato nei dati azienda).
     * Ogni riga: {codice, descrizione, tipo, codicePadre (o null se mastro), ruoloDefault (o null)}.
     * L'ordine e' significativo: i mastri precedono sempre i loro conti/sottoconti.
     */
    private static final String[][] TEMPLATE_GENERICO = {
            {"10", "IMMOBILIZZAZIONI", "ATTIVITA", null, null},
            {"10.01", "Immobilizzazioni immateriali", "ATTIVITA", "10", null},
            {"10.02", "Immobilizzazioni materiali", "ATTIVITA", "10", null},
            {"10.02.01", "Impianti e macchinari", "ATTIVITA", "10.02", null},
            {"10.02.02", "Attrezzature industriali e commerciali", "ATTIVITA", "10.02", null},
            {"10.02.03", "Mobili e arredi", "ATTIVITA", "10.02", null},
            {"10.02.04", "Automezzi", "ATTIVITA", "10.02", null},
            {"10.03", "Immobilizzazioni finanziarie", "ATTIVITA", "10", null},

            {"20", "RIMANENZE", "ATTIVITA", null, null},
            {"20.01", "Rimanenze di magazzino", "ATTIVITA", "20", "RIMANENZE"},

            {"30", "CREDITI", "ATTIVITA", null, null},
            {"30.01", "Crediti verso clienti", "ATTIVITA", "30", "CREDITI_CLIENTI"},
            {"30.02", "Crediti verso l'Erario", "ATTIVITA", "30", null},
            {"30.03", "Crediti diversi", "ATTIVITA", "30", null},

            {"40", "DISPONIBILITA' LIQUIDE", "ATTIVITA", null, null},
            {"40.01", "Cassa contanti", "ATTIVITA", "40", "CASSA"},
            {"40.02", "Banca c/c", "ATTIVITA", "40", "BANCA"},

            {"50", "PATRIMONIO NETTO", "PATRIMONIO_NETTO", null, null},
            {"50.01", "Capitale sociale", "PATRIMONIO_NETTO", "50", null},
            {"50.02", "Riserve", "PATRIMONIO_NETTO", "50", null},
            {"50.03", "Utili (perdite) portati a nuovo", "PATRIMONIO_NETTO", "50", null},
            {"50.04", "Utile (perdita) d'esercizio", "PATRIMONIO_NETTO", "50", "UTILE_ESERCIZIO"},

            {"60", "FONDI E TFR", "PASSIVITA", null, null},
            {"60.01", "Fondo TFR", "PASSIVITA", "60", null},
            {"60.02", "Fondi rischi ed oneri", "PASSIVITA", "60", null},

            {"70", "DEBITI", "PASSIVITA", null, null},
            {"70.01", "Debiti verso fornitori", "PASSIVITA", "70", "DEBITI_FORNITORI"},
            {"70.02", "Debiti verso banche", "PASSIVITA", "70", null},
            {"70.03", "Debiti tributari", "PASSIVITA", "70", null},
            {"70.04", "Debiti verso istituti previdenziali", "PASSIVITA", "70", null},
            {"70.05", "Debiti verso dipendenti e collaboratori", "PASSIVITA", "70", null},
            {"70.06", "Debiti diversi", "PASSIVITA", "70", null},

            {"80", "RICAVI", "RICAVO", null, null},
            {"80.01", "Ricavi delle vendite", "RICAVO", "80", "RICAVI_VENDITE"},
            {"80.02", "Ricavi per prestazioni di servizi", "RICAVO", "80", null},
            {"80.03", "Altri ricavi e proventi", "RICAVO", "80", null},
            {"80.04", "Proventi finanziari", "RICAVO", "80", null},

            {"90", "COSTI", "COSTO", null, null},
            {"90.01", "Costi per materie prime e merci", "COSTO", "90", "COSTI_ACQUISTI"},
            {"90.02", "Costi per servizi", "COSTO", "90", null},
            {"90.03", "Costi per godimento beni di terzi", "COSTO", "90", null},
            {"90.04", "Costo del personale", "COSTO", "90", null},
            {"90.05", "Ammortamenti", "COSTO", "90", null},
            {"90.06", "Oneri diversi di gestione", "COSTO", "90", null},
            {"90.07", "Oneri finanziari", "COSTO", "90", null},
            {"90.08", "Imposte dell'esercizio", "COSTO", "90", null},

            {"95", "IVA", "IVA", null, null},
            {"95.01", "IVA a credito", "IVA", "95", "IVA_CREDITO"},
            {"95.02", "IVA a debito", "IVA", "95", "IVA_DEBITO"},
            {"95.03", "IVA c/liquidazione", "IVA", "95", null},
    };

    // Conti aggiuntivi/rimossi rispetto al TEMPLATE_GENERICO, specifici per settore merceologico.
    // Vengono accodati dopo il generico: essendo tutti figli di mastri gia' presenti nel generico
    // (10/20/30/70/90...), l'ordine di inserimento resta valido (padre sempre prima del figlio).

    private static final String[][] EXTRA_COMMERCIO = {
            {"20.02", "Rimanenze merci c/rivendita", "ATTIVITA", "20", null},
            {"90.01.01", "Acquisti merci c/rivendita", "COSTO", "90.01", null},
    };

    private static final String[][] EXTRA_SERVIZI_AGGIUNTE = {
            {"90.02.01", "Consulenze professionali", "COSTO", "90.02", null},
            {"90.02.02", "Subappalti e collaborazioni", "COSTO", "90.02", null},
    };
    // In SERVIZI il magazzino di norma non e' pertinente: si rimuove il mastro RIMANENZE (20, con i suoi figli)
    // e gli asset tipici di produzione/commercio (impianti/macchinari, attrezzature).
    private static final String[] ESCLUSI_SERVIZI = {"20", "10.02.01", "10.02.02"};

    private static final String[][] EXTRA_PRODUZIONE = {
            {"20.02", "Rimanenze semilavorati", "ATTIVITA", "20", null},
            {"20.03", "Rimanenze prodotti finiti", "ATTIVITA", "20", null},
            {"90.04.01", "Manodopera diretta", "COSTO", "90.04", null},
    };

    private static final String[][] EXTRA_EDILIZIA = {
            {"20.02", "Lavori in corso su ordinazione", "ATTIVITA", "20", null},
            {"30.04", "Ritenute a garanzia attive", "ATTIVITA", "30", null},
            {"70.07", "Anticipi da committenti", "PASSIVITA", "70", null},
            {"70.08", "Ritenute a garanzia passive", "PASSIVITA", "70", null},
    };

    /**
     * Restituisce il template di piano dei conti piu' adatto al settore merceologico indicato,
     * derivandolo dal template generico con piccole aggiunte/rimozioni mirate.
     */
    private List<String[]> risolviTemplate(String settore) {
        List<String[]> base = new ArrayList<>(Arrays.asList(TEMPLATE_GENERICO));
        if (settore == null) {
            return base;
        }
        switch (settore) {
            case "COMMERCIO":
                base.addAll(Arrays.asList(EXTRA_COMMERCIO));
                return base;
            case "SERVIZI": {
                List<String[]> filtrato = new ArrayList<>();
                for (String[] riga : base) {
                    boolean escluso = false;
                    for (String codiceEscluso : ESCLUSI_SERVIZI) {
                        if (riga[0].equals(codiceEscluso) || riga[0].startsWith(codiceEscluso + ".")) {
                            escluso = true;
                            break;
                        }
                    }
                    if (!escluso) {
                        filtrato.add(riga);
                    }
                }
                filtrato.addAll(Arrays.asList(EXTRA_SERVIZI_AGGIUNTE));
                return filtrato;
            }
            case "PRODUZIONE":
                base.addAll(Arrays.asList(EXTRA_PRODUZIONE));
                return base;
            case "EDILIZIA":
                base.addAll(Arrays.asList(EXTRA_EDILIZIA));
                return base;
            default:
                return base;
        }
    }

    @Transactional(rollbackFor = Throwable.class)
    public void delete(long idUser, long id) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        dao.delete(idUser, id);
    }

    public PianoContoDto getById(long id) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        return dao.getById(id);
    }

    public List<PianoContoDto> getList(String strToSearch) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        return dao.getList(strToSearch);
    }

    @Transactional(rollbackFor = Throwable.class)
    public long insert(PianoContoDto dto) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        if (dao.isExistentCodice(dto.getCodice(), 0)) {
            throw new SQLException("Esiste gia' un conto con codice " + dto.getCodice());
        }
        return dao.insert(dto);
    }

    public boolean isEmpty() throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        return dao.count() == 0;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void update(PianoContoDto dto) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        if (dao.isExistentCodice(dto.getCodice(), dto.getId())) {
            throw new SQLException("Esiste gia' un conto con codice " + dto.getCodice());
        }
        dao.update(dto);
    }

    /**
     * Importa il set di conti standard nel piano dei conti del tenant corrente.
     * Puo' essere invocato solo se il piano dei conti e' vuoto, per non generare duplicati/conflitti
     * di codice con conti gia' personalizzati dall'utente.
     */
    @Transactional(rollbackFor = Throwable.class)
    public int importaStandard(long idUser) throws SQLException {
        PianoDeiContiDao dao = new PianoDeiContiDao(jdbcTemplate);
        if (dao.count() > 0) {
            throw new SQLException("Il piano dei conti non e' vuoto: l'importazione del set standard e' consentita solo su un piano dei conti vuoto.");
        }

        DatiAziendaDto datiAzienda = new DatiAziendaDao(jdbcTemplate).getDatiAzienda();
        String settore = datiAzienda != null ? datiAzienda.getSettoreMerceologico() : null;
        List<String[]> template = risolviTemplate(settore);

        Map<String, Long> idByCodice = new HashMap<>();
        int inserted = 0;
        for (String[] riga : template) {
            String codice = riga[0];
            String descrizione = riga[1];
            String tipo = riga[2];
            String codicePadre = riga[3];
            String ruoloDefault = riga[4];

            PianoContoDto dto = new PianoContoDto();
            dto.setCodice(codice);
            dto.setDescrizione(descrizione);
            dto.setTipo(tipo);
            dto.setRuoloDefault(ruoloDefault);
            dto.setIdPadre(codicePadre == null ? null : idByCodice.get(codicePadre));
            dto.setBloccato(1);
            dto.setUserCreated(idUser);

            long id = dao.insert(dto);
            idByCodice.put(codice, id);
            inserted++;
        }
        return inserted;
    }

    /**
     * Settore merceologico configurato nei dati azienda (o null se non impostato), usato dal
     * frontend per mostrare quale template di piano dei conti verra' proposto.
     */
    public String getSettoreCorrente() throws SQLException {
        DatiAziendaDto datiAzienda = new DatiAziendaDao(jdbcTemplate).getDatiAzienda();
        return datiAzienda != null ? datiAzienda.getSettoreMerceologico() : null;
    }
}
