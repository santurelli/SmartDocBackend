package it.tinna.smartdoc.shared.dto.contabilita;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;

import it.tinna.smartdoc.shared.dto.BaseDto;

/**
 * Bilancio (Stato Patrimoniale + Conto Economico) di un anno, alla data dell'ultima registrazione
 * disponibile. Se l'anno non e' ancora chiuso, l'utile/perdita e' "provvisorio" (calcolato sui
 * movimenti fin qui registrati, non ancora consolidato con una scrittura di chiusura).
 */
@SuppressWarnings("serial")
public class BilancioDto extends BaseDto {

    @Expose
    private int                            anno;

    @Expose
    private boolean                        esercizioChiuso;

    @Expose
    private List<ChiusuraEsercizioRigaDto> attivo;

    @Expose
    private List<ChiusuraEsercizioRigaDto> passivo;

    @Expose
    private BigDecimal                     totaleAttivo;

    @Expose
    private BigDecimal                     totalePassivo;

    @Expose
    private List<ChiusuraEsercizioRigaDto> costi;

    @Expose
    private List<ChiusuraEsercizioRigaDto> ricavi;

    @Expose
    private BigDecimal                     totaleCosti;

    @Expose
    private BigDecimal                     totaleRicavi;

    @Expose
    private BigDecimal                     utilePerdita;

    public int getAnno() {
        return anno;
    }

    public void setAnno(int anno) {
        this.anno = anno;
    }

    public boolean isEsercizioChiuso() {
        return esercizioChiuso;
    }

    public void setEsercizioChiuso(boolean esercizioChiuso) {
        this.esercizioChiuso = esercizioChiuso;
    }

    public List<ChiusuraEsercizioRigaDto> getAttivo() {
        return attivo;
    }

    public void setAttivo(List<ChiusuraEsercizioRigaDto> attivo) {
        this.attivo = attivo;
    }

    public List<ChiusuraEsercizioRigaDto> getPassivo() {
        return passivo;
    }

    public void setPassivo(List<ChiusuraEsercizioRigaDto> passivo) {
        this.passivo = passivo;
    }

    public BigDecimal getTotaleAttivo() {
        return totaleAttivo;
    }

    public void setTotaleAttivo(BigDecimal totaleAttivo) {
        this.totaleAttivo = totaleAttivo;
    }

    public BigDecimal getTotalePassivo() {
        return totalePassivo;
    }

    public void setTotalePassivo(BigDecimal totalePassivo) {
        this.totalePassivo = totalePassivo;
    }

    public List<ChiusuraEsercizioRigaDto> getCosti() {
        return costi;
    }

    public void setCosti(List<ChiusuraEsercizioRigaDto> costi) {
        this.costi = costi;
    }

    public List<ChiusuraEsercizioRigaDto> getRicavi() {
        return ricavi;
    }

    public void setRicavi(List<ChiusuraEsercizioRigaDto> ricavi) {
        this.ricavi = ricavi;
    }

    public BigDecimal getTotaleCosti() {
        return totaleCosti;
    }

    public void setTotaleCosti(BigDecimal totaleCosti) {
        this.totaleCosti = totaleCosti;
    }

    public BigDecimal getTotaleRicavi() {
        return totaleRicavi;
    }

    public void setTotaleRicavi(BigDecimal totaleRicavi) {
        this.totaleRicavi = totaleRicavi;
    }

    public BigDecimal getUtilePerdita() {
        return utilePerdita;
    }

    public void setUtilePerdita(BigDecimal utilePerdita) {
        this.utilePerdita = utilePerdita;
    }
}
