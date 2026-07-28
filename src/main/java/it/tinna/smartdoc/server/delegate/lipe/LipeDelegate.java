package it.tinna.smartdoc.server.delegate.lipe;

import it.tinna.smartdoc.server.dao.datiazienda.DatiAziendaDao;
import it.tinna.smartdoc.server.dao.lipe.LipeDao;
import it.tinna.smartdoc.server.delegate.BaseDelegate;
import it.tinna.smartdoc.shared.constants.Periodi;
import it.tinna.smartdoc.shared.dto.datiazienda.DatiAziendaDto;
import it.tinna.smartdoc.shared.dto.lipe.LipeAggregateDto;
import it.tinna.smartdoc.shared.dto.lipe.LipeDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;

@Service("lipeDelegate")
@Transactional(readOnly = true)
public class LipeDelegate extends BaseDelegate {

    private static final Logger log = LoggerFactory.getLogger(LipeDelegate.class);

    private static final String LIPE_NAMESPACE = "http://ivaservizi.agenziaentrate.gov.it/docs/xsd/valori/2017/v2";

    // jdbcTemplate iniettato da BaseDelegate via @Autowired


    /**
     * Calcola i dati IVA del trimestre e li restituisce come DTO pre-compilato.
     * I campi integrativi (debiti/crediti precedenti, acconto, ecc.) vengono
     * inizializzati a zero — l'utente li completa sul frontend prima di generare l'XML.
     */
    public LipeDto calcolaAnteprima(int anno, int trimestre) throws SQLException {
        Periodi periodo = trimestreToperiodo(trimestre);

        LipeDao lipeDao = new LipeDao(jdbcTemplate);
        LipeAggregateDto agg = lipeDao.getAggregatoTrimestrale(anno, periodo);

        DatiAziendaDao daDao = new DatiAziendaDao(jdbcTemplate);
        DatiAziendaDto azienda = daDao.getDatiAzienda();

        LipeDto dto = new LipeDto();
        dto.setAnno(anno);
        dto.setTrimestre(trimestre);

        BigDecimal imponibileVendite  = nvl(agg.getImponibileVendite());
        BigDecimal ivaEsigibile       = nvl(agg.getIvaEsigibile());
        BigDecimal imponibileAcquisti = nvl(agg.getImponibileAcquisti());
        BigDecimal ivaDetratta        = nvl(agg.getIvaDetratta());

        dto.setTotaleOperazioniAttive(imponibileVendite);
        dto.setIvaEsigibile(ivaEsigibile);
        dto.setTotaleOperazioniPassive(imponibileAcquisti);
        dto.setIvaDetratta(ivaDetratta);

        BigDecimal diff = ivaEsigibile.subtract(ivaDetratta).setScale(2, RoundingMode.HALF_UP);
        if (diff.compareTo(BigDecimal.ZERO) >= 0) {
            dto.setIvaDovuta(diff);
        } else {
            dto.setIvaCredito(diff.abs());
        }

        // Importo da versare = ivaDovuta (i campi integrativi sono ancora a zero)
        dto.setImportoDaVersare(dto.getIvaDovuta());

        // Anagrafica azienda
        if (azienda != null) {
            dto.setPartitaIva(StringUtils.defaultString(azienda.getPartitaIva()));
            dto.setCodiceFiscale(StringUtils.defaultString(azienda.getCodiceFiscale()));
            dto.setDenominazione(StringUtils.defaultString(azienda.getDenominazione()));
            dto.setCitta(StringUtils.defaultString(azienda.getCitta()));
            dto.setProvincia(StringUtils.defaultString(azienda.getProvincia()));
        }

        return dto;
    }

    /**
     * Genera l'XML LIPE pronto per la trasmissione all'Agenzia delle Entrate.
     * Riceve il DTO completo con i campi integrativi già valorizzati dall'utente.
     */
    public String generaXml(LipeDto dto) {
        // Ricalcola importoDaVersare / importoACredito in base a tutti i campi
        BigDecimal base = nvl(dto.getIvaDovuta())
                .add(nvl(dto.getDebitoIvaPrecedente()))
                .add(nvl(dto.getInteressiDovuti()))
                .subtract(nvl(dto.getIvaCredito()))
                .subtract(nvl(dto.getCreditoIvaPrecedente()))
                .subtract(nvl(dto.getCreditoIvaAnnoPrecedente()))
                .subtract(nvl(dto.getVersamentiAutoUE()))
                .subtract(nvl(dto.getCreditiImposta()))
                .subtract(nvl(dto.getAcconto()))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal importoDaVersare = base.compareTo(BigDecimal.ZERO) > 0 ? base : BigDecimal.ZERO.setScale(2);
        BigDecimal importoACredito  = base.compareTo(BigDecimal.ZERO) < 0 ? base.abs() : BigDecimal.ZERO.setScale(2);

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
        xml.append("<n:Comunicazione xmlns:n=\"").append(LIPE_NAMESPACE).append("\">\n");

        // Intestazione
        xml.append("  <Intestazione>\n");
        xml.append("    <CodiceFiscale>").append(escapeXml(dto.getCodiceFiscale())).append("</CodiceFiscale>\n");
        xml.append("    <Anno>").append(dto.getAnno()).append("</Anno>\n");
        xml.append("  </Intestazione>\n");

        // DatiContribuente
        xml.append("  <DatiContribuente>\n");
        xml.append("    <IdFiscaleIVA>\n");
        xml.append("      <IdPaese>IT</IdPaese>\n");
        xml.append("      <IdCodice>").append(escapeXml(dto.getPartitaIva())).append("</IdCodice>\n");
        xml.append("    </IdFiscaleIVA>\n");
        xml.append("    <CodiceFiscale>").append(escapeXml(dto.getCodiceFiscale())).append("</CodiceFiscale>\n");
        xml.append("    <DatiAnagrafici>\n");
        xml.append("      <Denominazione>").append(escapeXml(dto.getDenominazione())).append("</Denominazione>\n");
        xml.append("    </DatiAnagrafici>\n");
        xml.append("    <DomicilioFiscale>\n");
        xml.append("      <Comune>").append(escapeXml(dto.getCitta())).append("</Comune>\n");
        if (StringUtils.isNotBlank(dto.getProvincia())) {
            xml.append("      <Provincia>").append(escapeXml(dto.getProvincia())).append("</Provincia>\n");
        }
        xml.append("    </DomicilioFiscale>\n");
        xml.append("  </DatiContribuente>\n");

        // DatiLiquidazioni
        xml.append("  <DatiLiquidazioni>\n");
        xml.append("    <DatiLiquidazione>\n");
        xml.append("      <Trimestre>").append(dto.getTrimestre()).append("</Trimestre>\n");

        // DatiContabili
        xml.append("      <DatiContabili>\n");
        xml.append("        <TotaleOperazioniAttive>").append(fmt(nvl(dto.getTotaleOperazioniAttive()))).append("</TotaleOperazioniAttive>\n");
        xml.append("        <TotaleOperazioniPassive>").append(fmt(nvl(dto.getTotaleOperazioniPassive()))).append("</TotaleOperazioniPassive>\n");
        xml.append("        <IvaEsigibile>").append(fmt(nvl(dto.getIvaEsigibile()))).append("</IvaEsigibile>\n");
        xml.append("        <IvaDetratta>").append(fmt(nvl(dto.getIvaDetratta()))).append("</IvaDetratta>\n");
        if (nvl(dto.getIvaDovuta()).compareTo(BigDecimal.ZERO) > 0) {
            xml.append("        <IvaDovuta>").append(fmt(dto.getIvaDovuta())).append("</IvaDovuta>\n");
        } else {
            xml.append("        <IvaCredito>").append(fmt(nvl(dto.getIvaCredito()))).append("</IvaCredito>\n");
        }
        xml.append("      </DatiContabili>\n");

        // DatiIntegrativi
        BigDecimal subTotale = nvl(dto.getIvaDovuta())
                .add(nvl(dto.getDebitoIvaPrecedente()))
                .add(nvl(dto.getInteressiDovuti()))
                .setScale(2, RoundingMode.HALF_UP);

        xml.append("      <DatiIntegrativi>\n");
        xml.append("        <SubTotaleIvaDovuta>").append(fmt(subTotale)).append("</SubTotaleIvaDovuta>\n");
        xml.append("        <DebitoIvaPrecedente>").append(fmt(nvl(dto.getDebitoIvaPrecedente()))).append("</DebitoIvaPrecedente>\n");
        xml.append("        <CreditoIvaPrecedente>").append(fmt(nvl(dto.getCreditoIvaPrecedente()))).append("</CreditoIvaPrecedente>\n");
        xml.append("        <CreditoIvaAnnoPrecedente>").append(fmt(nvl(dto.getCreditoIvaAnnoPrecedente()))).append("</CreditoIvaAnnoPrecedente>\n");
        xml.append("        <VersamentiAutoUE>").append(fmt(nvl(dto.getVersamentiAutoUE()))).append("</VersamentiAutoUE>\n");
        xml.append("        <CreditiImposta>").append(fmt(nvl(dto.getCreditiImposta()))).append("</CreditiImposta>\n");
        xml.append("        <InteressiDovuti>").append(fmt(nvl(dto.getInteressiDovuti()))).append("</InteressiDovuti>\n");
        xml.append("        <Acconto>").append(fmt(nvl(dto.getAcconto()))).append("</Acconto>\n");
        if (importoDaVersare.compareTo(BigDecimal.ZERO) > 0) {
            xml.append("        <ImportoDaVersare>").append(fmt(importoDaVersare)).append("</ImportoDaVersare>\n");
        } else {
            xml.append("        <ImportoACredito>").append(fmt(importoACredito)).append("</ImportoACredito>\n");
        }
        xml.append("      </DatiIntegrativi>\n");

        xml.append("    </DatiLiquidazione>\n");
        xml.append("  </DatiLiquidazioni>\n");
        xml.append("</n:Comunicazione>");

        return xml.toString();
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Periodi trimestreToperiodo(int trimestre) {
        return switch (trimestre) {
            case 1 -> Periodi.PRIMO_TIMESTRE;
            case 2 -> Periodi.SECONDO_TIMESTRE;
            case 3 -> Periodi.TERZO_TRIMESTRE;
            case 4 -> Periodi.QUARTO_TRIMESTRE;
            default -> throw new IllegalArgumentException("Trimestre non valido: " + trimestre);
        };
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }

    private String fmt(BigDecimal v) {
        return String.format(java.util.Locale.US, "%.2f", v);
    }

    private String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
