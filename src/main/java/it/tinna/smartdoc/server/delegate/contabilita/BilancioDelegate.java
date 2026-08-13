package it.tinna.smartdoc.server.delegate.contabilita;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.server.delegate.datiazienda.DatiAziendaDelegate;
import it.tinna.smartdoc.server.util.ReportLoader;
import it.tinna.smartdoc.server.util.IOUtility;
import it.tinna.smartdoc.shared.dto.contabilita.BilancioDto;
import it.tinna.smartdoc.shared.dto.contabilita.BilancioSezioneStampaDto;
import it.tinna.smartdoc.shared.dto.contabilita.ChiusuraEsercizioPreviewDto;
import it.tinna.smartdoc.shared.dto.contabilita.ChiusuraEsercizioRigaDto;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.DocumentoWrapperDto;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/**
 * Bilancio (Stato Patrimoniale + Conto Economico) di un anno: riusa lo stesso calcolo di
 * ChiusuraEsercizioDelegate.anteprima() - saldo di apertura + movimenti dell'anno per ogni conto -
 * riorganizzandolo in Attivo/Passivo e Costi/Ricavi invece che in "conti da azzerare/riportare".
 *
 * Funziona anche per un esercizio NON ancora chiuso (bilancio provvisorio): l'utile/perdita calcolato
 * sui movimenti dell'anno viene sommato al Passivo (aumenta il Patrimonio Netto) cosi' che Attivo e
 * Passivo tornino sempre in pareggio, anche prima che la chiusura formale lo consolidi sul conto
 * "Utile (perdita) d'esercizio".
 *
 * Semplificazione nota: i conti di tipo IVA non hanno un lato Attivo/Passivo fisso nel piano dei conti
 * (dipende da chi vince la compensazione), quindi vengono smistati in base al segno del saldo (Dare
 * positivo -> Attivo, come un credito; Avere/negativo -> Passivo, come un debito).
 */
@Transactional(readOnly = true)
@Service
public class BilancioDelegate extends BaseDelegate {

    @Autowired
    private ChiusuraEsercizioDelegate chiusuraEsercizioDelegate;

    @Autowired
    private DatiAziendaDelegate datiaziendaDelegate;

    public BilancioDto genera(int anno) throws SQLException {
        ChiusuraEsercizioPreviewDto preview = chiusuraEsercizioDelegate.anteprima(anno);

        List<ChiusuraEsercizioRigaDto> attivo = new ArrayList<>();
        List<ChiusuraEsercizioRigaDto> passivo = new ArrayList<>();
        BigDecimal totaleAttivo = BigDecimal.ZERO;
        BigDecimal totalePassivo = BigDecimal.ZERO;

        for (ChiusuraEsercizioRigaDto r : preview.getRighePatrimoniali()) {
            boolean isAttivo;
            if ("ATTIVITA".equals(r.getTipoConto())) {
                isAttivo = true;
            } else if ("PASSIVITA".equals(r.getTipoConto()) || "PATRIMONIO_NETTO".equals(r.getTipoConto())) {
                isAttivo = false;
            } else {
                // IVA: nessun lato fisso, decide il segno del saldo.
                isAttivo = r.getSaldo().compareTo(BigDecimal.ZERO) > 0;
            }
            if (isAttivo) {
                totaleAttivo = totaleAttivo.add(r.getSaldo());
                attivo.add(r);
            } else {
                // Convenzione Dare-Avere: un conto Passivo/PN "sano" ha saldo negativo (Avere).
                // Lo presento come valore positivo, piu' leggibile per chi legge il bilancio.
                r.setSaldo(r.getSaldo().negate());
                totalePassivo = totalePassivo.add(r.getSaldo());
                passivo.add(r);
            }
        }

        List<ChiusuraEsercizioRigaDto> costi = new ArrayList<>();
        List<ChiusuraEsercizioRigaDto> ricavi = new ArrayList<>();
        BigDecimal totaleCosti = BigDecimal.ZERO;
        BigDecimal totaleRicavi = BigDecimal.ZERO;

        for (ChiusuraEsercizioRigaDto r : preview.getRigheEconomiche()) {
            if ("COSTO".equals(r.getTipoConto())) {
                totaleCosti = totaleCosti.add(r.getSaldo());
                costi.add(r);
            } else {
                r.setSaldo(r.getSaldo().negate());
                totaleRicavi = totaleRicavi.add(r.getSaldo());
                ricavi.add(r);
            }
        }

        // L'utile/perdita del periodo aumenta (o riduce, se negativo) il Patrimonio Netto: se l'anno e'
        // gia' stato chiuso formalmente e' gia' incluso nel saldo riportato del conto Utile d'esercizio
        // (righeEconomiche sara' vuota, quindi qui vale zero e non lo si somma due volte); se l'anno e'
        // ancora aperto, questo e' il pareggio "provvisorio" prima della chiusura.
        totalePassivo = totalePassivo.add(preview.getUtilePerdita());

        BilancioDto bilancio = new BilancioDto();
        bilancio.setAnno(anno);
        bilancio.setEsercizioChiuso(preview.isGiaChiuso());
        bilancio.setAttivo(attivo);
        bilancio.setPassivo(passivo);
        bilancio.setTotaleAttivo(totaleAttivo);
        bilancio.setTotalePassivo(totalePassivo);
        bilancio.setCosti(costi);
        bilancio.setRicavi(ricavi);
        bilancio.setTotaleCosti(totaleCosti);
        bilancio.setTotaleRicavi(totaleRicavi);
        bilancio.setUtilePerdita(preview.getUtilePerdita());
        return bilancio;
    }

    /**
     * Stampa PDF del bilancio (Jasper): un unico subreport "sezione" (report_stampa/bilancio_sezione.jrxml)
     * viene ripetuto 4 volte - una per Attivo/Passivo/Costi/Ricavi - guidato da un piccolo datasource
     * "fittizio" di 4 record (BilancioSezioneStampaDto), ciascuno dei quali seleziona via espressione
     * quale lista/totale passargli. Vedi report_stampa/bilancio.jrxml per il dettaglio.
     */
    public DocumentoWrapperDto esportaPdf(int anno) {
        try {
            BilancioDto bilancio = genera(anno);
            DatiAziendaDto datiAzienda = datiaziendaDelegate.getDatiAzienda();

            Map<String, Object> params = new HashMap<>();
            params.put("datiazienda", datiAzienda);
            params.put("anno", anno);
            params.put("statoEsercizio", bilancio.isEsercizioChiuso() ? "Chiuso" : "Aperto (bilancio provvisorio)");
            params.put("utilePerdita", bilancio.getUtilePerdita());
            params.put("dsAttivo", new JRBeanCollectionDataSource(bilancio.getAttivo()));
            params.put("dsPassivo", new JRBeanCollectionDataSource(bilancio.getPassivo()));
            params.put("dsCosti", new JRBeanCollectionDataSource(bilancio.getCosti()));
            params.put("dsRicavi", new JRBeanCollectionDataSource(bilancio.getRicavi()));
            params.put("totAttivo", bilancio.getTotaleAttivo());
            params.put("totPassivo", bilancio.getTotalePassivo());
            params.put("totCosti", bilancio.getTotaleCosti());
            params.put("totRicavi", bilancio.getTotaleRicavi());

            JasperReport report = ReportLoader.getReport("bilancio.jrxml");
            JasperReport subreportSezione = ReportLoader.getReport("bilancio_sezione.jrxml");
            params.put("SUBREPORT_SEZIONE", subreportSezione);

            List<BilancioSezioneStampaDto> sezioni = Arrays.asList(
                    new BilancioSezioneStampaDto("ATTIVO"),
                    new BilancioSezioneStampaDto("PASSIVO"),
                    new BilancioSezioneStampaDto("COSTI"),
                    new BilancioSezioneStampaDto("RICAVI"));

            byte[] bytes = JasperRunManager.runReportToPdf(report, params, new JRBeanCollectionDataSource(sezioni));

            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(bytes);
            result.setNome(IOUtility.getValidFilename("bilancio_" + anno) + ".pdf");
            return result;
        } catch (Exception e) {
            _log.error("Errore nella generazione del pdf del bilancio {}", anno, e);
            DocumentoWrapperDto result = new DocumentoWrapperDto();
            result.setFlusso(new byte[0]);
            result.setNome("blank.pdf");
            return result;
        }
    }
}
