package it.tinna.smartdoc.shared.dto.lipe;

import java.math.BigDecimal;

/**
 * Dati per la Comunicazione Liquidazione Periodica IVA (LIPE).
 * I campi "contabili" sono calcolati dal DB; i campi "integrativi" sono inseriti manualmente dall'utente.
 */
public class LipeDto {

    private Integer anno;
    private Integer trimestre; // 1-4

    // --- Dati contabili (calcolati dal DB) ---
    private BigDecimal totaleOperazioniAttive  = BigDecimal.ZERO;
    private BigDecimal ivaEsigibile            = BigDecimal.ZERO;
    private BigDecimal totaleOperazioniPassive = BigDecimal.ZERO;
    private BigDecimal ivaDetratta             = BigDecimal.ZERO;
    /** IvaEsigibile - IvaDetratta se > 0, altrimenti 0 */
    private BigDecimal ivaDovuta               = BigDecimal.ZERO;
    /** IvaDetratta - IvaEsigibile se > 0, altrimenti 0 */
    private BigDecimal ivaCredito              = BigDecimal.ZERO;

    // --- Dati integrativi (inseriti dall'utente prima di generare l'XML) ---
    private BigDecimal debitoIvaPrecedente       = BigDecimal.ZERO;
    private BigDecimal creditoIvaPrecedente      = BigDecimal.ZERO;
    private BigDecimal creditoIvaAnnoPrecedente  = BigDecimal.ZERO;
    private BigDecimal versamentiAutoUE          = BigDecimal.ZERO;
    private BigDecimal creditiImposta            = BigDecimal.ZERO;
    private BigDecimal interessiDovuti           = BigDecimal.ZERO;
    private BigDecimal acconto                   = BigDecimal.ZERO;
    /** Calcolato: ivaDovuta + debitoIvaPrecedente + interessiDovuti - creditoIvaPrecedente - creditoIvaAnnoPrecedente - versamentiAutoUE - creditiImposta - acconto */
    private BigDecimal importoDaVersare          = BigDecimal.ZERO;
    private BigDecimal importoACredito           = BigDecimal.ZERO;

    // --- Dati anagrafici azienda (dal DB) ---
    private String partitaIva;
    private String codiceFiscale;
    private String denominazione;
    private String citta;
    private String provincia;

    public Integer getAnno() { return anno; }
    public void setAnno(Integer anno) { this.anno = anno; }

    public Integer getTrimestre() { return trimestre; }
    public void setTrimestre(Integer trimestre) { this.trimestre = trimestre; }

    public BigDecimal getTotaleOperazioniAttive() { return totaleOperazioniAttive; }
    public void setTotaleOperazioniAttive(BigDecimal v) { this.totaleOperazioniAttive = v; }

    public BigDecimal getIvaEsigibile() { return ivaEsigibile; }
    public void setIvaEsigibile(BigDecimal v) { this.ivaEsigibile = v; }

    public BigDecimal getTotaleOperazioniPassive() { return totaleOperazioniPassive; }
    public void setTotaleOperazioniPassive(BigDecimal v) { this.totaleOperazioniPassive = v; }

    public BigDecimal getIvaDetratta() { return ivaDetratta; }
    public void setIvaDetratta(BigDecimal v) { this.ivaDetratta = v; }

    public BigDecimal getIvaDovuta() { return ivaDovuta; }
    public void setIvaDovuta(BigDecimal v) { this.ivaDovuta = v; }

    public BigDecimal getIvaCredito() { return ivaCredito; }
    public void setIvaCredito(BigDecimal v) { this.ivaCredito = v; }

    public BigDecimal getDebitoIvaPrecedente() { return debitoIvaPrecedente; }
    public void setDebitoIvaPrecedente(BigDecimal v) { this.debitoIvaPrecedente = v; }

    public BigDecimal getCreditoIvaPrecedente() { return creditoIvaPrecedente; }
    public void setCreditoIvaPrecedente(BigDecimal v) { this.creditoIvaPrecedente = v; }

    public BigDecimal getCreditoIvaAnnoPrecedente() { return creditoIvaAnnoPrecedente; }
    public void setCreditoIvaAnnoPrecedente(BigDecimal v) { this.creditoIvaAnnoPrecedente = v; }

    public BigDecimal getVersamentiAutoUE() { return versamentiAutoUE; }
    public void setVersamentiAutoUE(BigDecimal v) { this.versamentiAutoUE = v; }

    public BigDecimal getCreditiImposta() { return creditiImposta; }
    public void setCreditiImposta(BigDecimal v) { this.creditiImposta = v; }

    public BigDecimal getInteressiDovuti() { return interessiDovuti; }
    public void setInteressiDovuti(BigDecimal v) { this.interessiDovuti = v; }

    public BigDecimal getAcconto() { return acconto; }
    public void setAcconto(BigDecimal v) { this.acconto = v; }

    public BigDecimal getImportoDaVersare() { return importoDaVersare; }
    public void setImportoDaVersare(BigDecimal v) { this.importoDaVersare = v; }

    public BigDecimal getImportoACredito() { return importoACredito; }
    public void setImportoACredito(BigDecimal v) { this.importoACredito = v; }

    public String getPartitaIva() { return partitaIva; }
    public void setPartitaIva(String v) { this.partitaIva = v; }

    public String getCodiceFiscale() { return codiceFiscale; }
    public void setCodiceFiscale(String v) { this.codiceFiscale = v; }

    public String getDenominazione() { return denominazione; }
    public void setDenominazione(String v) { this.denominazione = v; }

    public String getCitta() { return citta; }
    public void setCitta(String v) { this.citta = v; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String v) { this.provincia = v; }
}
