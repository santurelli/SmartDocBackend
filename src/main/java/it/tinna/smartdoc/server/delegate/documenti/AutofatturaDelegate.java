package it.tinna.smartdoc.server.delegate.documenti;

import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.documenti.FattureFornitoreDao;
import it.tinna.smartdoc.server.dao.fornitori.FornitoriDao;
import it.tinna.smartdoc.server.dao.nazioni.NazioniDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.documenti.FatturaFornitoreDto;
import it.tinna.smartdoc.shared.dto.documenti.ProdottoDocumentoDto;
import it.tinna.smartdoc.shared.dto.fornitori.FornitoreDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@Service("autofatturaDelegate")
public class AutofatturaDelegate extends BaseDelegate {

    private static final Set<String> TIPI_AUTOFATTURA = Set.of("TD17", "TD18", "TD19", "TD20", "TD28");

    public String generaXml(long idFatturaFornitore) throws SQLException, ParseException {
        FattureFornitoreDao dao = new FattureFornitoreDao(jdbcTemplate);
        FatturaFornitoreDto dto = dao.getById(idFatturaFornitore);
        if (dto == null) {
            throw new IllegalArgumentException("Fattura fornitore non trovata: " + idFatturaFornitore);
        }

        String tipoDoc = dto.getTipoDocumentoSdi();
        if (StringUtils.isBlank(tipoDoc) || !TIPI_AUTOFATTURA.contains(tipoDoc.toUpperCase())) {
            throw new IllegalArgumentException("La fattura fornitore " + idFatturaFornitore + " non è un'autofattura (tipo SDI: " + tipoDoc + ")");
        }
        tipoDoc = tipoDoc.toUpperCase();

        DatiAziendaDto azienda = new DatiAziendaDao(jdbcTemplate).getDatiAzienda();
        FornitoreDto fornitore = new FornitoriDao(jdbcTemplate).getById(dto.getIdFornitore());
        NazioniDao nazioniDao = new NazioniDao(jdbcTemplate);
        List<ProdottoDocumentoDto> prodotti = dao.getListProdottiById(idFatturaFornitore);

        // Progressivo invio — riuso il progressivo interno come identificativo univoco
        String progressivo = String.valueOf(idFatturaFornitore);

        // Data documento
        String dataDocStr = dto.getDataDocumento();
        Date dataDoc = FastDateFormat.getInstance("dd/MM/yyyy").parse(dataDocStr);
        String dataIso = FastDateFormat.getInstance("yyyy-MM-dd").format(dataDoc);

        // ISO nazione fornitore
        String nazioneFornitore = StringUtils.defaultIfBlank(
            fornitore != null ? fornitore.getNazione() : dto.getNazioneIntestazione(), "Italia");
        String isoFornitore = nazioniDao.getCodiceIsoByNome(nazioneFornitore);
        if (StringUtils.isBlank(isoFornitore)) isoFornitore = "IT";

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<p:FatturaElettronica versione=\"FPR12\" ");
        xml.append("xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\" ");
        xml.append("xmlns:p=\"http://ivaservizi.agenziaentrate.gov.it/docs/xsd/fatture/v1.2\" ");
        xml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");

        // ===== HEADER =====
        xml.append("  <FatturaElettronicaHeader>\n");
        xml.append("    <DatiTrasmissione>\n");
        xml.append("      <IdTrasmittente>\n");
        xml.append("        <IdPaese>IT</IdPaese>\n");
        xml.append("        <IdCodice>").append(escapeXml(azienda.getPartitaIva())).append("</IdCodice>\n");
        xml.append("      </IdTrasmittente>\n");
        xml.append("      <ProgressivoInvio>").append(progressivo).append("</ProgressivoInvio>\n");
        xml.append("      <FormatoTrasmissione>FPR12</FormatoTrasmissione>\n");
        xml.append("      <CodiceDestinatario>0000000</CodiceDestinatario>\n");
        xml.append("    </DatiTrasmissione>\n");

        // CedentePrestatore = fornitore (chi ha emesso il documento originale)
        xml.append("    <CedentePrestatore>\n");
        xml.append("      <DatiAnagrafici>\n");
        if (fornitore != null && StringUtils.isNotBlank(fornitore.getPartitaIva())) {
            xml.append("        <IdFiscaleIVA>\n");
            xml.append("          <IdPaese>").append(isoFornitore).append("</IdPaese>\n");
            xml.append("          <IdCodice>").append(escapeXml(fornitore.getPartitaIva())).append("</IdCodice>\n");
            xml.append("        </IdFiscaleIVA>\n");
        }
        if (fornitore != null && StringUtils.isNotBlank(fornitore.getCodiceFiscale())) {
            xml.append("        <CodiceFiscale>").append(escapeXml(fornitore.getCodiceFiscale().toUpperCase())).append("</CodiceFiscale>\n");
        }
        xml.append("        <Anagrafica>\n");
        String denomFornitore = fornitore != null ? fornitore.getDenominazione() : dto.getIndirizzoIntestazione();
        xml.append("          <Denominazione>").append(escapeXml(StringUtils.defaultIfBlank(denomFornitore, "FORNITORE"))).append("</Denominazione>\n");
        xml.append("        </Anagrafica>\n");
        xml.append("        <RegimeFiscale>RF01</RegimeFiscale>\n");
        xml.append("      </DatiAnagrafici>\n");
        xml.append("      <Sede>\n");
        xml.append("        <Indirizzo>").append(escapeXml(StringUtils.defaultIfBlank(dto.getIndirizzoIntestazione(), "NON SPECIFICATO"))).append("</Indirizzo>\n");
        xml.append("        <CAP>").append(StringUtils.defaultIfBlank(dto.getCapIntestazione(), "00000")).append("</CAP>\n");
        xml.append("        <Comune>").append(escapeXml(StringUtils.defaultIfBlank(dto.getCittaIntestazione(), "NON SPECIFICATO"))).append("</Comune>\n");
        if (StringUtils.isNotBlank(dto.getProvinciaIntestazione())) {
            xml.append("        <Provincia>").append(dto.getProvinciaIntestazione().toUpperCase()).append("</Provincia>\n");
        }
        xml.append("        <Nazione>").append(isoFornitore).append("</Nazione>\n");
        xml.append("      </Sede>\n");
        xml.append("    </CedentePrestatore>\n");

        // CessionarioCommittente = azienda (noi, il cessionario che emette l'autofattura)
        xml.append("    <CessionarioCommittente>\n");
        xml.append("      <DatiAnagrafici>\n");
        xml.append("        <IdFiscaleIVA>\n");
        xml.append("          <IdPaese>IT</IdPaese>\n");
        xml.append("          <IdCodice>").append(escapeXml(azienda.getPartitaIva())).append("</IdCodice>\n");
        xml.append("        </IdFiscaleIVA>\n");
        if (StringUtils.isNotBlank(azienda.getCodiceFiscale())) {
            xml.append("        <CodiceFiscale>").append(escapeXml(azienda.getCodiceFiscale().toUpperCase())).append("</CodiceFiscale>\n");
        }
        xml.append("        <Anagrafica>\n");
        xml.append("          <Denominazione>").append(escapeXml(azienda.getDenominazione())).append("</Denominazione>\n");
        xml.append("        </Anagrafica>\n");
        xml.append("      </DatiAnagrafici>\n");
        xml.append("      <Sede>\n");
        xml.append("        <Indirizzo>").append(escapeXml(azienda.getIndirizzo())).append("</Indirizzo>\n");
        xml.append("        <CAP>").append(azienda.getCap()).append("</CAP>\n");
        xml.append("        <Comune>").append(escapeXml(azienda.getCitta())).append("</Comune>\n");
        if (StringUtils.isNotBlank(azienda.getProvincia())) {
            xml.append("        <Provincia>").append(azienda.getProvincia().toUpperCase()).append("</Provincia>\n");
        }
        xml.append("        <Nazione>IT</Nazione>\n");
        xml.append("      </Sede>\n");
        xml.append("    </CessionarioCommittente>\n");
        xml.append("  </FatturaElettronicaHeader>\n");

        // ===== BODY =====
        xml.append("  <FatturaElettronicaBody>\n");
        xml.append("    <DatiGenerali>\n");
        xml.append("      <DatiGeneraliDocumento>\n");
        xml.append("        <TipoDocumento>").append(tipoDoc).append("</TipoDocumento>\n");
        xml.append("        <Divisa>EUR</Divisa>\n");
        xml.append("        <Data>").append(dataIso).append("</Data>\n");
        String numero = dto.getNumDocumento() != null ? dto.getNumDocumento().toString() : "1";
        if (StringUtils.isNotBlank(dto.getParticella())) {
            numero = numero + (dto.getParticella().startsWith("/") ? dto.getParticella() : "/" + dto.getParticella());
        }
        xml.append("        <Numero>").append(escapeXml(numero)).append("</Numero>\n");
        xml.append("      </DatiGeneraliDocumento>\n");

        // Riferimento al documento originale del fornitore
        if (StringUtils.isNotBlank(dto.getNumeroDocumentoFornitore())) {
            xml.append("      <DatiDocumentiCorrelati>\n");
            xml.append("        <IdDocumento>").append(escapeXml(dto.getNumeroDocumentoFornitore())).append("</IdDocumento>\n");
            if (StringUtils.isNotBlank(dto.getDataDocumentoFornitore())) {
                try {
                    Date dataForn = FastDateFormat.getInstance("dd/MM/yyyy").parse(dto.getDataDocumentoFornitore());
                    xml.append("        <Data>").append(FastDateFormat.getInstance("yyyy-MM-dd").format(dataForn)).append("</Data>\n");
                } catch (ParseException ignored) { }
            }
            xml.append("      </DatiDocumentiCorrelati>\n");
        }
        xml.append("    </DatiGenerali>\n");

        // DatiBeniServizi
        xml.append("    <DatiBeniServizi>\n");

        // Raggruppa per aliquota IVA per il riepilogo
        java.util.Map<Double, BigDecimal[]> riepilogoPerAliquota = new java.util.LinkedHashMap<>();

        if (prodotti == null || prodotti.isEmpty()) {
            // Fallback: una riga generica con totale documento
            xml.append("      <DettaglioLinee>\n");
            xml.append("        <NumeroLinea>1</NumeroLinea>\n");
            xml.append("        <Descrizione>Operazione</Descrizione>\n");
            xml.append("        <PrezzoUnitario>0.00</PrezzoUnitario>\n");
            xml.append("        <PrezzoTotale>0.00</PrezzoTotale>\n");
            xml.append("        <AliquotaIVA>22.00</AliquotaIVA>\n");
            xml.append("      </DettaglioLinee>\n");
        } else {
            int linea = 1;
            for (ProdottoDocumentoDto p : prodotti) {
                String desc = StringUtils.defaultIfBlank(
                    StringUtils.isNotBlank(p.getFmDescrizione()) ? p.getFmDescrizione() : p.getDescProdotto(),
                    "Prestazione");
                double aliquota = p.getPercentualeIva() != null ? p.getPercentualeIva() : 0.0;
                double imponibile = p.getTotaleSenzaIva() != null ? p.getTotaleSenzaIva() : 0.0;

                xml.append("      <DettaglioLinee>\n");
                xml.append("        <NumeroLinea>").append(linea++).append("</NumeroLinea>\n");
                xml.append("        <Descrizione>").append(escapeXml(desc)).append("</Descrizione>\n");
                xml.append("        <Quantita>").append(formatDecimal(p.getQuantita() != null ? p.getQuantita() : 1.0)).append("</Quantita>\n");
                xml.append("        <PrezzoUnitario>").append(formatDecimal(imponibile)).append("</PrezzoUnitario>\n");
                xml.append("        <PrezzoTotale>").append(formatDecimal(imponibile)).append("</PrezzoTotale>\n");
                xml.append("        <AliquotaIVA>").append(formatDecimal(aliquota)).append("</AliquotaIVA>\n");
                xml.append("      </DettaglioLinee>\n");

                riepilogoPerAliquota.merge(aliquota, new BigDecimal[]{
                    BigDecimal.valueOf(imponibile),
                    BigDecimal.valueOf(imponibile * aliquota / 100.0)
                }, (a, b) -> new BigDecimal[]{a[0].add(b[0]), a[1].add(b[1])});
            }
        }

        // DatiRiepilogo per aliquota
        if (riepilogoPerAliquota.isEmpty()) {
            xml.append("      <DatiRiepilogo>\n");
            xml.append("        <AliquotaIVA>22.00</AliquotaIVA>\n");
            xml.append("        <ImponibileImporto>0.00</ImponibileImporto>\n");
            xml.append("        <Imposta>0.00</Imposta>\n");
            xml.append("        <EsigibilitaIVA>I</EsigibilitaIVA>\n");
            xml.append("      </DatiRiepilogo>\n");
        } else {
            for (java.util.Map.Entry<Double, BigDecimal[]> entry : riepilogoPerAliquota.entrySet()) {
                BigDecimal imponibile = entry.getValue()[0];
                BigDecimal imposta = entry.getValue()[1];
                xml.append("      <DatiRiepilogo>\n");
                xml.append("        <AliquotaIVA>").append(formatDecimal(entry.getKey())).append("</AliquotaIVA>\n");
                xml.append("        <ImponibileImporto>").append(formatDecimal(imponibile.doubleValue())).append("</ImponibileImporto>\n");
                xml.append("        <Imposta>").append(formatDecimal(imposta.doubleValue())).append("</Imposta>\n");
                xml.append("        <EsigibilitaIVA>I</EsigibilitaIVA>\n");
                xml.append("      </DatiRiepilogo>\n");
            }
        }

        xml.append("    </DatiBeniServizi>\n");
        xml.append("  </FatturaElettronicaBody>\n");
        xml.append("</p:FatturaElettronica>\n");

        return xml.toString();
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private String formatDecimal(double value) {
        return String.format("%.2f", value);
    }
}
